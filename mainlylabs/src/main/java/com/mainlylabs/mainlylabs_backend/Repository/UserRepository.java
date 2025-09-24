package com.mainlylabs.mainlylabs_backend.Repository;

import com.mainlylabs.mainlylabs_backend.Entity.LdapUser;
import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends LdapRepository<LdapUser> {
    LdapUser findByEmail(String email);
}
