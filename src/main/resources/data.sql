INSERT INTO teams (name, created_at, updated_at, version)
VALUES ('Brazil', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
INSERT INTO teams (name, created_at, updated_at,  version)
VALUES ('Argentina', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
INSERT INTO teams (name, created_at, updated_at,  version)
VALUES ('Germany', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
INSERT INTO teams (name, created_at, updated_at,  version)
VALUES ('Italy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
INSERT INTO teams (name, created_at, updated_at,  version)
VALUES ('France', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
INSERT INTO teams (name, created_at, updated_at, version)
VALUES ('Spain', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
INSERT INTO teams (name, created_at, updated_at,  version)
VALUES ('Netherlands', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
INSERT INTO teams (name, created_at, updated_at,  version)
VALUES ('England', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
INSERT INTO teams (name, created_at, updated_at,  version)
VALUES ('Portugal', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
INSERT INTO teams (name, created_at, updated_at,  version)
VALUES ('Belgium', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);

INSERT INTO games (created_at, updated_at, game_status, away_team_id, home_team_id, home_team_score, away_team_score, version)
VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'NEW', 1, 2, null, null, 1);
INSERT INTO games (created_at, updated_at, game_status, away_team_id, home_team_id, home_team_score, away_team_score, version)
VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'IN_PROGRESS', 3, 5, 1, 1, 1);
INSERT INTO games (created_at, updated_at, game_status, away_team_id, home_team_id, home_team_score, away_team_score, version)
VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'FINISHED', 3, 5, 1, 1, 1);