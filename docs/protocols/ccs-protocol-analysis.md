# CCS protocol analysis

Sprint 1 task: analyse the HSE Common Conditions Service (CCS) pharmacist assessment forms to design the protocol format for the protocol engine (FR10–FR15, FR25).

**Source:** Irish Pharmacy Union *Pharmacist Assessment Forms*, Version 1, January 2026, for all seven CCS conditions. Criteria below are summarised for design purposes; the official HSE protocols and assessment forms remain the clinical reference.

## 1. Common structure

All seven forms share the same nine sections, which makes a single data-driven engine possible.

| # | Section | Common content | Varies by condition |
|---|---|---|---|
| 1 | Personal details | Name, address, Eircode, PPSN, contact, scheme (GMS / DPS / GP Visit / Private), DOB, sex, GP details (optional) | **Age limits**, **sex limits**, whether a parent/guardian is recorded (only conditions that can treat under-16s) |
| 2 | Presenting complaint | Free-text symptoms; "medication already tried?" yes/no + list | Condition name only |
| 3 | Medical history | Existing conditions, pregnancy, breastfeeding, renal and hepatic impairment, allergy status, existing medication, known antimicrobial resistance | **What pregnancy/breastfeeding triggers**, **extra condition-specific questions** |
| 4 | Red flag and referral criteria | Checklists grouped by severity tier, then "Are there any red flag or referral criteria present?" + reason | **Which tiers exist**, the criteria in each, whether a tier allows an initial limited supply |
| 5 | Review of symptoms | Checklist(s) of typical signs and symptoms, then "Are symptoms typical?" yes → continue / no → refer | **Symptom lists and groups** (e.g. children, babies, rhinitis vs conjunctivitis) |
| 6 | Treatment options | Inclusion criteria met → pharmacist prescribing and/or advice and counselling | UTI spells out an explicit **all-of** inclusion rule |
| 7 | Patient declaration | Understanding, opportunity to ask questions, data recording/sharing, data protection; consent to share; dispensing choice; signatures and date | **Follow-up wording** (14 days, 72 hours, 7 days…), guardian signature only where under-16s are treated |
| 8 | Consultation outcome | Referral (8.1: A&E / GP / other) · self-care · OTC product supplied · POM prescription (8.2) · patient declined (reason) | **Medicines (8.2)**: products, doses, age bands, lines of therapy, modifiers, quantities, adjuncts |
| 9 | Pharmacist information | Name, PSI number, signature, pharmacy address, Eircode, date | — |

## 2. Red flag tiers

Section 4 groups criteria into up to four tiers. **The section numbers are not consistent across forms** (e.g. allergic rhinitis has no urgent tier, so its "4.2" is a GP referral), so the protocol format must define tiers by **action**, not by number.

| Tier | Action | Prescribing allowed? |
|---|---|---|
| `EMERGENCY` | Emergency referral to hospital ED / emergency services (ambulance) | No |
| `URGENT` | Urgent medical assessment (treating service, GP, GP out of hours or ED) | No — **except Shingles**, where an initial limited supply may be given |
| `REFER` | Referral to GP (or dental practitioner / other relevant practitioner) | No |
| `REFER_LIMITED_SUPPLY` | Referral, but the pharmacist may give an **initial limited supply** if clinically appropriate | Yes (limited) |

## 3. Differences between conditions

| Condition | Refer by age | Refer by sex | Pregnancy | Guardian | Tiers | Extra history | Medicines (8.2) | Follow-up wording |
|---|---|---|---|---|---|---|---|---|
| **Cold sores** | < 1 month (corrected) | — | Refer GP | Yes | E, U, R, RLS | — | Aciclovir 5% cream | Worse or > 14 days |
| **Allergic rhinitis & conjunctivitis** | < 2 years | — | Refer GP | Yes | E, R, RLS (no urgent tier) | Resistance to previous treatment | **Rhinitis:** nasal sprays (INCS 2nd gen; INCS + intranasal antihistamine; INCS 1st gen **over 18 only**; intranasal antihistamine) and minimally sedating oral antihistamines (2nd gen: cetirizine, loratadine, bilastine; 3rd gen: desloratadine, fexofenadine, levocetirizine; tablets and oral solutions). **Conjunctivitis** (eye only, or not responding to oral antihistamine): sodium cromoglicate, ketotifen or olopatadine eye drops | Worse or does not improve |
| **Impetigo** (non-bullous) | < 2 months (corrected) | — | Recorded; **breastfeeding with lesion on breast → refer** | Yes | E, U, R, RLS | — | Fusidic acid 2% cream **or** sodium fusidate 2% ointment, 5–7 days; **quantity 15 g (30 g only if 15 g unavailable)** | Worse or no improvement within 72 hours |
| **Oral thrush** | < 1 month (corrected) | — | Refer GP | Yes | E, U, R, RLS (referral may be to **dental practitioner**) | — | Nystatin suspension or miconazole oral gel, **dose by age band**; miconazole 2% cream for **breastfeeding mothers** (nipple/areola) | Worse or > 14 days |
| **Shingles** | < 18 years | — | **Pregnancy or breastfeeding → refer** | No | E (many), U (**limited supply allowed**), R, RLS | Chickenpox history / chickenpox or shingles vaccine | **1st line** valaciclovir, **2nd** aciclovir, **3rd** famciclovir; each with an **"if immunocompromised"** extension; renal dose-reduction note | Worse or fails to respond |
| **Uncomplicated lower UTI** | < 16 **or** > 64 years | **Male → refer** | **Urgent medical assessment** | No | E (several), U, R (no limited-supply tier) | Already on prophylactic antibiotics for UTI → refer | **First choice:** nitrofurantoin immediate release 50 mg 6-hourly or prolonged release 100 mg 12-hourly, 3 days. **Alternative first choice** if nitrofurantoin unsuitable (e.g. breastfeeding with infant < 1 month or G6PD deficiency, allergy, eGFR < 45): trimethoprim 200 mg 12-hourly, 3 days | Worse or no improvement within 48 hours |
| **Vulvovaginal thrush** | < 16 **or** > 60 years | **Male → refer** | Refer GP | No | E, U, R (no limited-supply tier); recurrent candidiasis at **pharmacist discretion** | — | Clotrimazole pessary 500 mg / 200 mg / 100 mg or econazole 150 mg pessary; **optional adjunct** clotrimazole cream or clotrimazole + hydrocortisone cream | Worse or no improvement within 7 days, or recurs within 2 months |

E = emergency, U = urgent, R = refer (no prescribing), RLS = refer with initial limited supply.

## 4. Consultation flow rules

These rules apply to every condition and will be implemented once in the protocol engine. The step flow follows the Startup Lab web prototype; the main change is that the red-flag decision is made automatically from the ticked criteria (FR13) instead of a manual Yes/No answer.

1. **Automatic referral triggers.** Answers in sections 1 and 3 (age from DOB, sex, pregnancy, breastfeeding with lesion on breast, prophylactic UTI antibiotics) automatically tick the matching section 4 criterion, so a referral cannot be missed.
2. **Red flag decided automatically.** "Are there any red flag or referral criteria present?" is **Yes** whenever any section 4 criterion is ticked (in any tier, including initial limited supply), and a reason for referral is required.
3. **Red flag → jump to section 7.** If a red flag is present, sections 5 and 6 are skipped and the consultation goes straight to **7. Patient declaration**. Going back from section 7 returns to section 4. *(As in the prototype.)*
4. **Highest tier sets the referral.** If several criteria are ticked, the most severe tier decides the suggested referral destination: `EMERGENCY` (A&E) > `URGENT` (GP / out of hours / ED) > `REFER` (GP, dental practitioner or other) > `REFER_LIMITED_SUPPLY` (GP, with an initial limited supply allowed).
5. **Symptoms not typical → refer, but continue.** Answering "no" in section 5 requires a referral reason; the consultation then continues to section 6. *(As in the prototype.)*
6. **Outcome.** In section 8 the pharmacist selects **one or more** outcomes (referral, self-care, OTC product, POM prescription, patient declined). Referral requires a destination (8.1); a POM prescription requires a medicine (8.2). Where a red flag is present, **referral must be one of the outcomes**, and a prescription is only allowed if every ticked criterion permits an initial limited supply.
7. **Inclusion before prescribing.** Medicines in 8.2 can only be selected if the section 6 inclusion criteria are met (UTI: **all three** of dysuria, frequency and urgency **and** all exclusions absent; with only **one or two** of those symptoms the pathway is **"wait and see" for 48 hours** with advice and signposting, and no prescription), except for an initial limited supply under rule 6.
8. **Medicine filtering.** Only medicines valid for the patient are offered: age bands (e.g. oral thrush), population (e.g. breastfeeding mothers, over 18 only), and modifiers (e.g. immunocompromised extension for shingles).
9. **Guardian consent.** A parent/guardian name and signature are required when the patient is under 16 and the condition allows under-16s.

## 5. What the protocol format needs to support

| Need | Example |
|---|---|
| Patient eligibility rules (age with units, sex) | Cold sores < 1 month; UTI < 16 or > 64 years; UTI/VVT male |
| Shared field definitions reused by every protocol | Personal details, medical history, declaration, pharmacist information |
| Condition-specific extra questions | Shingles vaccine history; UTI prophylactic antibiotics |
| Conditional triggers from answers to criteria | Pregnancy → REFER (most) or URGENT (UTI) |
| Red-flag criteria grouped by tier, with a per-tier limited-supply flag | Shingles urgent tier allows limited supply |
| Criteria needing pharmacist judgement | VVT recurrent candidiasis ("pharmacist discretion") |
| Grouped symptom checklists and multi-choice "typical?" answers | Allergic rhinitis vs allergic conjunctivitis |
| All-of / count-based inclusion logic with alternative pathways | UTI: all 3 symptoms → prescribe; 1–2 symptoms → wait and see 48 hours |
| Medicine groups that depend on earlier answers | Allergic rhinitis vs allergic conjunctivitis medicines |
| Medicine suitability conditions and alternatives | UTI: trimethoprim if nitrofurantoin unsuitable (breastfeeding, allergy, eGFR < 45) |
| Medicines with lines of therapy, age bands, population, modifiers, quantities and adjuncts | Shingles lines; oral thrush age bands; impetigo quantity; VVT adjunct creams |
| Condition-specific declaration wording | 14 days / 72 hours / 7 days and 2 months |
| Version and source metadata | "Version 1, January 2026" (FR26: consultations keep the version used) |

## 6. Gaps to resolve

- Doses and quantities must be checked against the official HSE protocols before being entered as protocol data.
- Allergic rhinitis page 4 and UTI page 3 were rescanned on 21 October 2026 and are now included above.
