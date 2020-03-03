ALTER TABLE account ALTER COLUMN username SET NOT NULL;
ALTER TABLE account ADD CONSTRAINT username_unique UNIQUE (username);