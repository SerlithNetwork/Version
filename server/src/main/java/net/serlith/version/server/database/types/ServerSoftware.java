package net.serlith.version.server.database.types;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@NoArgsConstructor
@AllArgsConstructor
@Table(name = "version_server_software")
public class ServerSoftware {

    @Id
    public Long id;

    @Column("name")
    public String name;

    @Column("display_name")
    public String displayName;

}
