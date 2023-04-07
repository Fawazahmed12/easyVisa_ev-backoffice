ALTER TABLE legal_representative ADD COLUMN top_contributor_score integer NOT NULL DEFAULT 0;
ALTER TABLE legal_representative ADD COLUMN recent_contributor_score integer NOT NULL DEFAULT 0;
ALTER TABLE legal_representative ADD COLUMN base_contributor_score integer NOT NULL DEFAULT 0;
ALTER TABLE legal_representative ADD COLUMN random_score float(24) NOT NULL DEFAULT 0;
