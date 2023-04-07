UPDATE
    poverty_guideline
SET version = 2,
    year    = 2022
WHERE version = 1;

UPDATE
    poverty_guideline
SET base_price   = 15630,
    add_on_price = 5430
WHERE state = 'HAWAII';

UPDATE
    poverty_guideline
SET base_price   = 16990,
    add_on_price = 5900
WHERE state = 'ALASKA';

UPDATE
    poverty_guideline
SET base_price   = 13590,
    add_on_price = 4720
WHERE state not in ('ALASKA', 'HAWAII');