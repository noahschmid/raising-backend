ALTER TABLE investmentSector RENAME TO industry;
ALTER TABLE supervisionType RENAME TO support;
ALTER TABLE investmentSectorAssignment RENAME COLUMN investmentSectorId TO industryId;
ALTER TABLE investmentSectorAssignment RENAME TO industryAssignment;
ALTER TABLE supervisionTypeAssignment RENAME COLUMN supervisionTypeId TO supportId;
ALTER TABLE supervisionTypeAssignment RENAME TO supportAssignment;