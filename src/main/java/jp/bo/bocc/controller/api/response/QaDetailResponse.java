package jp.bo.bocc.controller.api.response;

import jp.bo.bocc.entity.ShtQa;
import jp.bo.bocc.entity.ShtTalkQa;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by DonBach on 4/25/2017.
 */
@NoArgsConstructor
@AllArgsConstructor
public class QaDetailResponse {
    @Getter @Setter
    private ShtQa qa;

    @Getter @Setter
    private LocalDateTime currentDateTime;

    @Getter @Setter
    List<ShtTalkQa> listTalkQas;
}
