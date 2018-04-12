package jp.bo.bocc.controller.api.response;

import jp.bo.bocc.entity.dto.ShmPostDTO;
import jp.bo.bocc.entity.dto.ShtTalkPurcDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

/**
 * Created by DonBach on 4/21/2017.
 */
@NoArgsConstructor
@AllArgsConstructor
public class TalkPurcResponse {
    @Getter @Setter
    private ShmPostDTO shmPostDTO;

    @Getter @Setter
    private Page<ShtTalkPurcDTO> listTalkPurc;
}
