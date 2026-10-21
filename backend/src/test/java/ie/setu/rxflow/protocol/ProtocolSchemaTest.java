package ie.setu.rxflow.protocol;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Checks that every bundled CCS protocol matches the protocol schema (FR25)
 * and that invalid protocol definitions are rejected (NFR13).
 */
class ProtocolSchemaTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static JsonSchema schema;

    @BeforeAll
    static void loadSchema() throws IOException {
        try (InputStream in = resource("protocols/protocol.schema.json")) {
            schema = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012).getSchema(in);
        }
    }

    @Test
    void everyBundledProtocolMatchesTheSchema() throws Exception {
        List<Path> protocols = bundledProtocols();
        assertFalse(protocols.isEmpty(), "No protocols found in protocols/conditions");

        for (Path protocol : protocols) {
            Set<ValidationMessage> errors = schema.validate(MAPPER.readTree(protocol.toFile()));
            assertTrue(errors.isEmpty(), protocol.getFileName() + " is invalid: " + errors);
        }
    }

    @Test
    void supportsTheMostComplexRulesAcrossAllConditions() throws IOException {
        try (InputStream in = resource("protocols/feature-coverage.json")) {
            Set<ValidationMessage> errors = schema.validate(MAPPER.readTree(in));
            assertTrue(errors.isEmpty(), "feature-coverage.json is invalid: " + errors);
        }
    }

    @Test
    void rejectsAnUnknownRedFlagTier() throws Exception {
        ObjectNode protocol = coldSores();
        ((ObjectNode) protocol.at("/redFlags/tiers/0")).put("tier", "CRITICAL");

        assertInvalid(protocol);
    }

    @Test
    void rejectsAProtocolWithoutRedFlags() throws Exception {
        ObjectNode protocol = coldSores();
        protocol.remove("redFlags");

        assertInvalid(protocol);
    }

    @Test
    void rejectsATriggerThatReferencesAnUnknownField() throws Exception {
        ObjectNode protocol = coldSores();
        ObjectNode trigger = (ObjectNode) protocol.at("/redFlags/tiers/1/criteria/0/trigger");
        trigger.put("field", "patient.age");

        assertInvalid(protocol);
    }

    @Test
    void rejectsAComparisonWithTwoOperators() throws Exception {
        ObjectNode protocol = coldSores();
        ObjectNode trigger = (ObjectNode) protocol.at("/redFlags/tiers/1/criteria/0/trigger");
        trigger.put("gt", 0);

        assertInvalid(protocol);
    }

    @Test
    void rejectsAMedicineWithoutADosage() throws Exception {
        ObjectNode protocol = coldSores();
        ArrayNode options = (ArrayNode) protocol.at("/medicines/groups/0/options");
        ((ObjectNode) options.get(0)).remove("dosage");

        assertInvalid(protocol);
    }

    private static void assertInvalid(JsonNode protocol) {
        assertFalse(schema.validate(protocol).isEmpty(), "Expected the protocol to be rejected");
    }

    private static ObjectNode coldSores() throws IOException {
        try (InputStream in = resource("protocols/conditions/cold-sores.json")) {
            return (ObjectNode) MAPPER.readTree(in);
        }
    }

    private static List<Path> bundledProtocols() throws IOException, URISyntaxException {
        Path dir = Path.of(ProtocolSchemaTest.class.getClassLoader().getResource("protocols/conditions").toURI());
        try (Stream<Path> files = Files.list(dir)) {
            return files.filter(p -> p.toString().endsWith(".json")).sorted().toList();
        }
    }

    private static InputStream resource(String name) {
        InputStream in = ProtocolSchemaTest.class.getClassLoader().getResourceAsStream(name);
        if (in == null) {
            throw new IllegalStateException("Missing resource: " + name);
        }
        return in;
    }
}
