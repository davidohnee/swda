/*
 * Copyright 2024 Roland Gisler, Hochschule Luzern - Informatik.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.hslu.swda.model.Student;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Testcases für Student Service. Verwendet Micronaut.io.
 */
final class StudentsMemoryTest {

    private Students students;

    @BeforeEach
    void beforeEach() {
        this.students = new StudentsMemory();
    }

    /**
     * Testcase für {@link ch.hslu.swda.business.StudentsMemory#getAll()}.
     */
    @Test
    void testGetAll() {
        assertThat(new StudentsMemory().getAll()).hasSize(3);
    }

    /**
     * Testcase für {@link ch.hslu.swda.business.StudentsMemory#getById(long)}.
     */
    @Test
    void testGetFirst() {
        final Student student = this.students.getById(1L);
        assertThat(student).isNotNull();
        assertThat(student.getId()).isEqualTo(1);
        assertThat(student.getFirstName()).isEqualTo("Herbert");
    }

    /**
     * Testcase für
     * {@link ch.hslu.swda.business.StudentsMemory#findByLastName(String)}.
     */
    @Test
    void testFindByNameNot() {
        assertThat(this.students.findByLastName("XXX")).hasSize(0);
    }

    /**
     * Testcase für
     * {@link ch.hslu.swda.business.StudentsMemory#findByLastName(String)}.
     */
    @Test
    void testFindByName() {
        final List<Student> list = this.students.findByLastName("Zweifel");
        assertThat(list).hasSize(1);
    }

    /**
     * Testcase für
     * {@link ch.hslu.swda.business.StudentsMemory#create(Student)}.
     */
    @Test
    void testCreate() {
        final Student student = students.create(new Student("Vorname", "Nachname", 1));
        assertAll("Student",
                () -> assertThat(student.getFirstName()).isEqualTo("Vorname"),
                () -> assertThat(student.getLastName()).isEqualTo("Nachname"),
                () -> assertThat(student.getMonthOfBirth()).isEqualTo(1)
        );
    }

    /**
     * Testcase für
     * {@link ch.hslu.swda.business.StudentsMemory#update(long, Student)}.
     */
    @Test
    void testUpdateExistingStudent() {
        final Student updated = students.update(1L, new Student("Vorname", "Nachname", 1));
        assertNotNull(updated);
        assertAll("Student",
                () -> assertThat(updated.getFirstName()).isEqualTo("Vorname"),
                () -> assertThat(updated.getLastName()).isEqualTo("Nachname"),
                () -> assertThat(updated.getMonthOfBirth()).isEqualTo(1)
        );
    }

    /**
     * Testcase für
     * {@link ch.hslu.swda.business.StudentsMemory#update(long, Student)}.
     */
    @Test
    void testUpdateNotExistingStudent() {
        final Student updated = students.update(200L, new Student("NewVorname", "NewNachname", 24));
        assertNotNull(updated);
        assertAll("Student",
                () -> assertThat(updated.getFirstName()).isEqualTo("NewVorname"),
                () -> assertThat(updated.getLastName()).isEqualTo("NewNachname"),
                () -> assertThat(updated.getMonthOfBirth()).isEqualTo(24)
        );
    }

    /**
     * Testcase für {@link ch.hslu.swda.business.StudentsMemory#delete(long)}.
     */
    @Test
    void testDeleteExistingStudent() {
        final boolean deleted = students.delete(1L);
        assertTrue(deleted);
        assertThat(students.getById(1L)).isNull();
        assertThat(students.getAll()).hasSize(2);
    }

    /**
     * Testcase für {@link ch.hslu.swda.business.StudentsMemory#delete(long)}.
     */
    @Test
    void testDeleteNotExistingStudent() {
        final boolean deleted = students.delete(100L);
        assertFalse(deleted);
        assertThat(students.getAll()).hasSize(3);
    }
}
