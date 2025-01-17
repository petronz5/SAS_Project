USE catering;

-- Creazione tabella SummarySheets
CREATE TABLE IF NOT EXISTS SummarySheets (
                                             id INT AUTO_INCREMENT PRIMARY KEY,
                                             service_id INT NOT NULL,
                                             event_id INT NOT NULL,
                                             FOREIGN KEY (service_id) REFERENCES Services(id) ON DELETE CASCADE,
                                             FOREIGN KEY (event_id) REFERENCES Events(id) ON DELETE CASCADE
);

-- Creazione tabella Tasks
CREATE TABLE IF NOT EXISTS Tasks (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     recipe_id INT NOT NULL,
                                     summary_sheet_id INT NOT NULL,
                                     portions INT DEFAULT 0,
                                     quantity VARCHAR(50),
                                     estimated_time INT DEFAULT 0,
                                     completed BOOLEAN DEFAULT FALSE,
                                     FOREIGN KEY (recipe_id) REFERENCES Recipes(id) ON DELETE CASCADE,
                                     FOREIGN KEY (summary_sheet_id) REFERENCES SummarySheets(id) ON DELETE CASCADE
);

-- Creazione tabella Cooks
CREATE TABLE IF NOT EXISTS Cooks (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     name VARCHAR(50) NOT NULL,
                                     badge INT UNIQUE NOT NULL
);

-- Creazione tabella Turns
CREATE TABLE IF NOT EXISTS Turns (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     expiration_date DATE NOT NULL,
                                     preparation_place VARCHAR(50),
                                     start_time TIME NOT NULL,
                                     end_time TIME NOT NULL,
                                     recurrence BOOLEAN DEFAULT FALSE,
                                     staff_limit INT DEFAULT 0,
                                     current_staff INT DEFAULT 0,
                                     end_date DATE NOT NULL
);

-- Creazione tabella TaskAssignments
CREATE TABLE IF NOT EXISTS TaskAssignments (
                                               id INT AUTO_INCREMENT PRIMARY KEY,
                                               task_id INT NOT NULL,
                                               turn_id INT NOT NULL,
                                               cook_id INT NOT NULL,
                                               quantity VARCHAR(50),
                                               estimated_time INT,
                                               portions INT,
                                               FOREIGN KEY (task_id) REFERENCES Tasks(id) ON DELETE CASCADE,
                                               FOREIGN KEY (turn_id) REFERENCES Turns(id) ON DELETE CASCADE,
                                               FOREIGN KEY (cook_id) REFERENCES Cooks(id) ON DELETE CASCADE
);

-- Inserimento dati iniziali nella tabella SummarySheets
INSERT INTO SummarySheets (id, service_id, event_id) VALUES
                                                         (1, 1, 1),
                                                         (2, 2, 2);

-- Inserimento dati iniziali nella tabella Tasks
INSERT INTO Tasks (id, recipe_id, summary_sheet_id, portions, quantity, estimated_time, completed) VALUES
                                                                                                       (1, 1, 1, 10, '5kg', 120, FALSE),
                                                                                                       (2, 2, 1, 20, '3kg', 60, TRUE);

-- Inserimento dati iniziali nella tabella Cooks
INSERT INTO Cooks (id, name, badge) VALUES
                                        (1, 'Paolo', 123),
                                        (2, 'Maria', 456),
                                        (3, 'Luigi', 789);

-- Inserimento dati iniziali nella tabella Turns
INSERT INTO Turns (id, expiration_date, preparation_place, start_time, end_time, recurrence, staff_limit, current_staff, end_date) VALUES
                                                                                                                                       (1, '2025-01-17', 'Main Kitchen', '08:00:00', '16:00:00', FALSE, 5, 0, '2025-01-18'),
                                                                                                                                       (2, '2025-01-18', 'Backup Kitchen', '09:00:00', '17:00:00', TRUE, 3, 1, '2025-01-19');

-- Inserimento dati iniziali nella tabella TaskAssignments
INSERT INTO TaskAssignments (id, task_id, turn_id, cook_id, quantity, estimated_time, portions) VALUES
                                                                                                    (1, 1, 1, 1, '5kg', 120, 10),
                                                                                                    (2, 2, 2, 2, '3kg', 60, 20);
