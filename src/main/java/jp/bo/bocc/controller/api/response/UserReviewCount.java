package jp.bo.bocc.controller.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by DonBach on 4/4/2017.
 */
@AllArgsConstructor
@NoArgsConstructor
public class UserReviewCount {

    @Getter @Setter
    private Long userId;

    @Getter @Setter
    private Long goodTypeNumber;

    @Getter @Setter
    private Long fairTypeNumber;

    @Setter @Getter
    private Long badTypeNumber;
}
