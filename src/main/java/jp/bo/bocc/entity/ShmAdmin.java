package jp.bo.bocc.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import java.util.Set;

/**
 * Created by Namlong on 3/14/2017.
 */
@Entity
@Table(name = "SHM_ADMIN")
@DynamicInsert @DynamicUpdate
@NoArgsConstructor @AllArgsConstructor
@Where(clause = "CMN_DELETE_FLAG=0")
public class ShmAdmin extends BaseEntity {

    @Getter @Setter
    @Id
    @Column(name = "ADMIN_ID")
    @SequenceGenerator(name = "seq", sequenceName = "SHM_ADMIN_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    @Access(AccessType.PROPERTY)
    private Long adminId;

    @Getter @Setter
    @Column(name = "ADMIN_NAME")
    private String adminName;

    @Getter @Setter
    @Column(name = "ADMIN_PWD")
    private String adminPwd;

    @Getter @Setter
    @Column(name = "ADMIN_EMAIL")
    private String adminEmail;

    @Getter @Setter
    @Column(name = "ADMIN_ROLE")
    private Integer adminRole;

    @Getter @Setter
    @Transient
    private String adminRoleTxt;

    @Transient
    @Getter @Setter
    private String pwdFresh;

    @Transient
    @Getter @Setter
    private String confirmAdminPwd;

    @Transient
    @Getter @Setter
    private Date formatEntryDate;

    public enum ADMIN_ROLE {
        SUPPER_ADMIN,
        ADMIN,
        SITE_PATROL,
        CUSTOMER_SUPPORT
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "shmAdmin")
   @Transient
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter @Setter
    private Set<ShmAdminNg> shmAdminNgs;
}
