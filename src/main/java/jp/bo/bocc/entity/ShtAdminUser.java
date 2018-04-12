package jp.bo.bocc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Namlong on 3/14/2017.
 */
@Entity
@Table(name = "SHT_ADMIN_USER")
@DynamicInsert @DynamicUpdate
@NoArgsConstructor @AllArgsConstructor
@Where(clause = "CMN_DELETE_FLAG=0")
public class ShtAdminUser extends BaseEntity {

    @Getter @Setter
    @Id
    @Column(name = "ADMIN_USER_ID")
    @SequenceGenerator(name = "seq", sequenceName = "SHT_ADMIN_USER_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    private Long adminUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter @Setter
    private ShmUser shmUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADMIN_ID")
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter @Setter
    private ShmAdmin shmAdmin;
}
