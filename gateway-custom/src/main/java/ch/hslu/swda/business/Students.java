/*
 * Copyright 2024, Roland Gisler, Hochschule Luzern - Informatik.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.hslu.swda.business;

import java.util.List;

import ch.hslu.swda.model.Student;

/**
 * Einfache Businesslogik für Verwaltung von Studenten.
 */
public interface Students {

    /**
     * Liefert einen Student.
     * @param id Id des Student.
     * @return Student.
     */
    Student getById(long id);

    /**
     * Sucht nach Studierenden.
     * @param lastName Nachname.
     * @return Liste von Student.
     */
    List<Student> findByLastName(String lastName);

    /**
     * Liefert alle Studierenden.
     * @return Liste von Student.
     */
    List<Student> getAll();

    /**
     * Erstellt einen (neuen) Student.
     * @param student Student.
     * @return Student.
     */
    Student create(Student student);

    /**
     * Speichert/aktualisiert einen Student.
     * @param id id.
     * @param student Student.
     * @return Student.
     */
    Student update(long id, Student student);

    /**
     * Löscht einen Student.
     * @param id Id des Studenten.
     * @return OK oder NOK.
     */
    boolean delete(long id);
}