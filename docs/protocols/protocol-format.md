# Protocol format (version 1)

Each CCS condition is a JSON file in [`backend/src/main/resources/protocols/conditions`](../../backend/src/main/resources/protocols/conditions), validated against [`protocol.schema.json`](../../backend/src/main/resources/protocols/protocol.schema.json) (JSON Schema 2020-12). Adding a condition means adding a file, not writing code (FR25, NFR25).

The design is based on the [CCS protocol analysis](ccs-protocol-analysis.md).

## What a protocol contains

Sections shared by every condition – personal details, the standard medical history questions, the standard declaration statements and pharmacist information – are built into the engine. A protocol only describes what varies:

| Property | Assessment form section | Purpose |
|---|---|---|
| `id`, `name`, `version`, `source` | – | Identity and versioning. Consultations keep the version they used (FR26). |
| `patient.allowsUnder16` | 1. Personal details | Whether parent/guardian details and consent apply. |
| `questions` | 3. Medical history | Condition-specific extra questions (e.g. shingles vaccine history). |
| `redFlags.tiers` | 4. Red flags and referral | Criteria grouped by tier, with automatic triggers. |
| `symptoms` | 5. Review of symptoms | Symptom checklists and the "are symptoms typical?" options. |
| `treatment.pathways` | 6. Treatment options | Inclusion criteria and the actions they allow. |
| `declaration.followUp` | 7. Patient declaration | Condition-specific follow-up wording. |
| `medicines` | 8.2 Medicine prescribed | Medicines, doses and when each is available. |

## Red flag tiers

Tiers are defined by the action required, because the form section numbers differ between conditions.

| Tier | Action | Severity |
|---|---|---|
| `EMERGENCY` | Emergency referral (A&E / ambulance) | Highest |
| `URGENT` | Urgent medical assessment | |
| `REFER` | Referral, no prescribing | |
| `REFER_LIMITED_SUPPLY` | Referral, initial limited supply allowed | Lowest |

Each tier states `allowsLimitedSupply` (Shingles allows it in the urgent tier) and the allowed referral destinations (`AE`, `GP`, `GP_OUT_OF_HOURS`, `DENTAL`, `OTHER`). A criterion can have a `trigger` so the engine ticks it automatically, and `pharmacistDiscretion` where the pharmacist may decide.

## Conditions

Triggers, `showWhen`, `when`, `availableWhen` and `applyWhen` use a small, side-effect-free expression language, so protocols contain no executable code.

| Expression | Meaning | Example |
|---|---|---|
| `{ "field": …, "eq": … }` | Comparison (`eq`, `ne`, `lt`, `lte`, `gt`, `gte`, `in`) | `{ "field": "patient.ageMonths", "lt": 1 }` |
| `{ "all": [ … ] }` | All conditions true | |
| `{ "any": [ … ] }` | At least one condition true | Age under 16 **or** over 64 |
| `{ "not": … }` | Negation | |
| `{ "count": [ … ], "gte": 1 }` | How many fields are true (`eq`, `gte`, `lte`) | UTI: 3 symptoms → prescribe; 1–2 → wait and see |

Fields the engine can evaluate:

| Field | Value |
|---|---|
| `patient.ageMonths`, `patient.ageYears` | Calculated from date of birth |
| `patient.sex` | `MALE` or `FEMALE` |
| `history.pregnant`, `history.breastfeeding`, `history.renalImpairment`, `history.hepaticImpairment`, `history.antimicrobialResistance` | Standard medical history answers |
| `questions.<id>` | A condition-specific question |
| `criteria.<id>` | Whether a red-flag criterion is ticked |
| `symptoms.<id>` | Whether a symptom is ticked |
| `symptoms.typical` | The selected "are symptoms typical?" option id |

## Medicines

Medicines are listed in groups with a `role` (`PRIMARY`, or `ADJUNCT` which can only be added alongside a primary medicine) and an optional `line` of therapy. A group or medicine can be limited with `availableWhen`; a medicine can have `extensions` (e.g. a longer course if immunocompromised) and `quantities` (e.g. 15 g or 30 g tube).

## Validation

`ProtocolSchemaTest` checks that every bundled protocol matches the schema, that a test fixture combining the most complex rules from all seven conditions is valid, and that invalid protocols (unknown tier, missing red flags, unknown field, malformed condition, medicine without dosage) are rejected.

Schema validation checks structure. Checks that need the protocol as a whole – such as every `criteria.<id>` reference pointing to a criterion that exists – will be added with the protocol engine in Sprint 3.
