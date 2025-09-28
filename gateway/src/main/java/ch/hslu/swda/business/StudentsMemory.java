/*
 * Copyright 2024 Roland Gisler, HSLU Informatik, Switzerland
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.hslu.swda.model.Student;

/**
 * Fake Implementation für Verwaltung von Studenten. Speichert die Daten nur in
 * einer Map (in Memory).
 */
@Singleton
public class StudentsMemory implements Students {
    private static final Logger LOG = LoggerFactory.getLogger(StudentsMemory.class);
    private int idInc = 0;
    private Map<Long, Student> students;

    /**
     * Erzeugt initial eine Liste mit drei Studenten.
     */
    public StudentsMemory() {
        this.students = new HashMap<>();
        this.students.put(1L, new Student(1, "Herbert", "Grone", 3));
        this.students.put(2L, new Student(2, "Babette", "Zweifel", 4));
        this.students.put(3L, new Student(3, "Markus", "Kaiser", 11));
        this.idInc = this.students.size();
        LOG.debug("API: Es wurden {} Studenten wurden erzeugt (Test-Daten).", idInc);
    }

    /**
     * @see ch.hslu.swda.business.Students#getById(long)
     */
    @Override
    public Student getById(final long id) {
        LOG.info("API: Student mit id={} gelesen.", id);
        return this.students.get(id);
    }

    /**
     * @see ch.hslu.swda.business.Students#findByLastName(java.lang.String)
     */
    @Override
    public List<Student> findByLastName(final String lastName) {
        List<Student> list = this.students.values().stream().filter(s -> s.getLastName().contains(lastName))
                .collect(Collectors.toList());
        LOG.info("API: Liste mit {} Studenten mit Nachname '{}' gefunden.", list.size(), lastName);
        return list;
    }

    /**
     * @see ch.hslu.swda.business.Students#getAll()
     */
    @Override
    public List<Student> getAll() {
        List<Student> list = this.students.values().stream().collect(Collectors.toList());
        LOG.info("API: Liste mit {} Studenten gelesen.", students.size());
        return list;
    }

    /**
     * @see ch.hslu.swda.business.Students#create(Student)
     */
    @Override
    public Student create(final Student student) {
        student.setId(++this.idInc);
        this.students.put((long) student.getId(), student);
        LOG.info("API: Student '{}' erstellt.", student);
        return student;
    }

    /**
     * @see ch.hslu.swda.business.Students#update(long, Student)
     */
    @Override
    public Student update(final long id, final Student student) {
        Student exists = this.students.get(id);
        if (exists != null) {
            exists.setLastName(student.getLastName());
            exists.setFirstName(student.getFirstName());
            exists.setMonthOfBirth(student.getMonthOfBirth());
            this.students.put(id, student);
            LOG.info("API: Student {} aktualisiert.", student);
        } else {
            exists = create(student);
        }
        return exists;
    }

    /**
     * @see ch.hslu.swda.business.Students#delete(long)
     */
    @Override
    public boolean delete(final long id) {
        final Student student = students.remove(id);
        LOG.info("API: Student mit id={} geloescht.", student);
        return student != null;
    }
}
