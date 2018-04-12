package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.dto.ShmUserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Namlong on 4/13/2017.
 */
public interface UserRepositoryCustom {

    /**
     * Get list user for patrol site.
     * @param processStatus
     * @param datetimepickerFrom
     * @param datetimepickerTo
     * @param patrolAdminNames
     * @param datetimepickerFinishFrom
     * @param datetimepickerFinishTo
     * @param pageable
     * @param imageServer
     * @param filterPendingStt
     * @param upadtedAfterCensoring
     * @return
     */
    Page<ShmUserDTO> getUserListForPatrolSite(String processStatus, Date datetimepickerFrom, Date datetimepickerTo,
                                              String patrolAdminNames, Date datetimepickerFinishFrom,
                                              Date datetimepickerFinishTo, Pageable pageable, String imageServer, boolean filterPendingStt, boolean upadtedAfterCensoring);

    List<ShmUser> getUserListByFirstNameAndLastName(List<String> userNames);

    List<ShmUser> getUserListByFirstNameAndLastName(String userNames);

    Map<String, List<Long>> findUserListRegistInMonth(LocalDate startDate, LocalDate endDate);

    Long getUserNumberRegisByDay(String day);

    Long getUserNumberRegisByMonth(String month);

    String getMinRegDate();

    List<String> getBsFromOtherSchema();

    List<ShmUser> getMissingLeftSyncUsersBeforeNow();
}
