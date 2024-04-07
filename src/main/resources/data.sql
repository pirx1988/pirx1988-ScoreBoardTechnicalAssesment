INSERT INTO teams (name, created_at, updated_at)
VALUES ('Brazil', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO teams (name, created_at, updated_at)
VALUES ('Argentina', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO teams (name, created_at, updated_at)
VALUES ('Germany', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO teams (name, created_at, updated_at)
VALUES ('Italy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO teams (name, created_at, updated_at)
VALUES ('France', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO teams (name, created_at, updated_at)
VALUES ('Spain', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO teams (name, created_at, updated_at)
VALUES ('Netherlands', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO teams (name, created_at, updated_at)
VALUES ('England', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO teams (name, created_at, updated_at)
VALUES ('Portugal', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO teams (name, created_at, updated_at)
VALUES ('Belgium', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO games (created_at, updated_at, game_status, away_team_id, home_team_id, home_team_score, away_team_score)
VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'NEW', 1, 2, null, null);
INSERT INTO games (created_at, updated_at, game_status, away_team_id, home_team_id, home_team_score, away_team_score)
VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'IN_PROGRESS', 3, 5, 1, 1);
INSERT INTO games (created_at, updated_at, game_status, away_team_id, home_team_id, home_team_score, away_team_score)
VALUES (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'FINISHED', 3, 5, 1, 1);