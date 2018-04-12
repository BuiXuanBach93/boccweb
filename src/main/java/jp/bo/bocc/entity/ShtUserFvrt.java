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
@Table(name = "SHT_USER_FVRT")
@DynamicInsert @DynamicUpdate
@NoArgsConstructor @AllArgsConstructor
@Where(clause = "CMN_DELETE_FLAG=0")
public class ShtUserFvrt extends BaseEntity {

    @Id
    @Column(name = "USER_FVRT_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SHT_USER_FVRT_SEQ")
    @SequenceGenerator(name = "SHT_USER_FVRT_SEQ", sequenceName = "SHT_USER_FVRT_SEQ", allocationSize = 1)
    @Getter @Setter
    private Long userFvrtId;

    @JoinColumn(name = "POST_ID")
    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter @Setter
    private ShmPost shmPost;

    @JoinColumn(name = "USER_ID")
    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter @Setter
    private ShmUser shmUser;

    @Column(name = "USER_FVRT_STATUS")
    @Getter @Setter
    @Enumerated(EnumType.ORDINAL)
    private UserFavoriteEnum userFvrtStatus;

    public enum UserFavoriteEnum{
        NONE, LIKED
    }
}
