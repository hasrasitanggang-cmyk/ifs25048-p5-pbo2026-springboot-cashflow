package org.delcom.ifs25048springbootcashflow.adapter.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.delcom.ifs25048springbootcashflow.domain.entity.Role;
import org.junit.jupiter.api.Test;

class JdbcUserRepositoryTest {

    @Test
    void parseRoleHandlesNullLegacyValidAndUnknownValues() {
        assertEquals(Role.USER, JdbcUserRepository.parseRole(null));
        assertEquals(Role.ADMIN, JdbcUserRepository.parseRole(" ROLE_ADMIN "));
        assertEquals(Role.USER, JdbcUserRepository.parseRole("USER"));
        assertEquals(Role.USER, JdbcUserRepository.parseRole("MANAGER"));
    }
}
