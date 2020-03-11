ALTER TABLE boardmember
DROP CONSTRAINT boardmember_startupid_fkey, 
ADD CONSTRAINT boardmember_startupid_fkey
FOREIGN KEY (startupid) 
REFERENCES startup(id)
ON DELETE CASCADE;

ALTER TABLE founder
RENAME COLUMN startipid TO startupid;

ALTER TABLE founder
DROP CONSTRAINT founder_startipid_fkey, 
ADD CONSTRAINT founder_startupid_fkey
FOREIGN KEY (startupid) 
REFERENCES startup(id)
ON DELETE CASCADE;

ALTER TABLE contact
DROP CONSTRAINT contact_startupid_fkey, 
ADD CONSTRAINT contact_startupid_fkey
FOREIGN KEY (startupid) 
REFERENCES startup(id)
ON DELETE CASCADE;

ALTER TABLE resetcode
DROP CONSTRAINT resetcodes_accountid_fkey, 
ADD CONSTRAINT resetcode_accountid_fkey
FOREIGN KEY (accountid) 
REFERENCES account(id)
ON DELETE CASCADE;

ALTER TABLE relationship
DROP CONSTRAINT relationship_startupid_fkey, 
ADD CONSTRAINT relationship_startupid_fkey
FOREIGN KEY (startupid) 
REFERENCES startup(id)
ON DELETE CASCADE;

ALTER TABLE relationship
DROP CONSTRAINT relationship_investorid_fkey, 
ADD CONSTRAINT relationship_investorid_fkey
FOREIGN KEY (investorid) 
REFERENCES investor(id)
ON DELETE CASCADE;

ALTER TABLE history
DROP CONSTRAINT history_relationshipid_fkey, 
ADD CONSTRAINT history_relationshipid_fkey
FOREIGN KEY (relationshipid) 
REFERENCES relationship(id)
ON DELETE CASCADE;