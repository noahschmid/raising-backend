ALTER TABLE country ADD CONSTRAINT uniquecntry_const UNIQUE(name);
ALTER TABLE continent ADD CONSTRAINT uniquecntnt_const UNIQUE(name);
ALTER TABLE label ADD CONSTRAINT uniquelbl_const UNIQUE(name);
ALTER TABLE industry ADD CONSTRAINT uniqueind_const UNIQUE(name);
ALTER TABLE investmentPhase ADD CONSTRAINT uniquephs_const UNIQUE(name);
ALTER TABLE support ADD CONSTRAINT uniquespprt_const UNIQUE(name);
ALTER TABLE investorType ADD CONSTRAINT uniquetype_const UNIQUE(name);