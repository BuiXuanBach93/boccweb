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
@Table(name = "SHT_ADMIN_POST")
@DynamicInsert @DynamicUpdate
@NoArgsConstructor @AllArgsConstructor
@Where(clause = "CMN_DELETE_FLAG=0")
public class ShtAdminPost extends BaseEntity {

    @Getter @Setter
    @Id
    @Column(name = "ADMIN_POST_ID")
    @SequenceGenerator(name = "seq", sequenceName = "SHT_ADMIN_POST_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    private Long adminPostId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID")
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter @Setter
    private ShmPost shmPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADMIN_ID")
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter @Setter
    private ShmAdmin shmAdmin;
}
