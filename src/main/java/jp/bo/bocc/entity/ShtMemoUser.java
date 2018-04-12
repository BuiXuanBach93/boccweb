package jp.bo.bocc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * @author NguyenThuong on 4/5/2017.
 */
@Entity
@Table(name = "SHT_MEMO_USER")
@DynamicInsert @DynamicUpdate
@NoArgsConstructor @AllArgsConstructor
public class ShtMemoUser extends BaseEntity {

    @Id
    @Column(name = "MEMO_USER_ID")
    @SequenceGenerator(name = "seq", sequenceName = "SHT_MEMO_USER_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    @Getter @Setter
    private Long id;

    @Getter @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ADMIN_ID")
    @NotFound(action = NotFoundAction.IGNORE)
    private ShmAdmin admin;

    @Getter @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID")
    @NotFound(action = NotFoundAction.IGNORE)
    private ShmUser user;

    @Getter @Setter
    @Column(name = "MEMO_CONTENT")
    private String content;

    @Getter @Setter
    @Transient
    private int sequentNumber;
}
