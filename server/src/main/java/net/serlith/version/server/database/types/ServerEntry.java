package net.serlith.version.server.database.types;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@NoArgsConstructor
@AllArgsConstructor
@Table(name = "version_server_entry")
public class ServerEntry {

    @Id
    public Long id;

    @Column("software_id")
    public Long software;

    @Column("version")
    public String version;

    @Column("build")
    public Integer build;

}
