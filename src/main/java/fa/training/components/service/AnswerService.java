package fa.training.components.service;

import fa.training.components.dto.AnswerDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AnswerService {
    /**
     * Find answer from database by ID
     * @param id of answer
     * @param isAdmin is current logged customer an admin
     * @return answer found or null
     */
    AnswerDTO findByID(String id, boolean isAdmin);

    /**
     * Find answer from database by question
     * @param questionID ID of question that answer belongs
     * @param isAdmin is current logged customer an admin
     * @param pageable a Pageable object
     * @return list of answer found or empty list
     */
    List<AnswerDTO> findByQuestion(String questionID, boolean isAdmin, Pageable pageable);

    /**
     * Find answer from database by owner
     * @param owner username of customer who own the answer
     * @param isAdmin is current logged customer an admin
     * @param pageable a Pageable object
     * @return list of answer found or empty list
     */
    List<AnswerDTO> findByOwner(String owner, boolean isAdmin, Pageable pageable);

    /**
     * Find all answers from database
     * @param isAdmin is current logged customer an admin
     * @param pageable a Pageable object
     * @return list of answer found or empty list
     */
    List<AnswerDTO> findAll(boolean isAdmin, Pageable pageable);

    /**
     * Create an answer and save to database
     * @param answerDTO answer to create
     * @return answer saved to database
     */
    AnswerDTO createAnswer(AnswerDTO answerDTO);

    /**
     * Edit an answer in database
     * @param answerDTO answer with update
     * @param owner username of current logged in customer
     * @param isAdmin is current logged customer an admin
     * @return edited answer
     */
    AnswerDTO editAnswer(AnswerDTO answerDTO, String owner, boolean isAdmin);

    /**
     * Delete an answer in database
     * @param id of answer to delete
     * @param owner username of current logged in customer
     * @param isAdmin is current logged customer an admin
     * @return deleted answer
     */
    AnswerDTO deleteAnswer(String id, String owner, boolean isAdmin);
}
