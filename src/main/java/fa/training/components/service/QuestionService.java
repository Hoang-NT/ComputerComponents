package fa.training.components.service;

import fa.training.components.dto.QuestionDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuestionService {
    /**
     * Find question from database by ID
     * @param id of question
     * @param isAdmin is current logged customer an admin
     * @return question found or null
     */
    QuestionDTO findByID(String id, boolean isAdmin);

    /**
     * Find question from database by product ID
     * @param productID ID of product that question belongs
     * @param isAdmin is current logged customer an admin
     * @param pageable a Pageable object
     * @return list of question found or empty list
     */
    List<QuestionDTO> findByProduct(String productID, boolean isAdmin, Pageable pageable);

    /**
     * Find question from database by owner
     * @param owner username of customer who own the question
     * @param isAdmin is current logged customer an admin
     * @param pageable a Pageable object
     * @return list of question found or empty list
     */
    List<QuestionDTO> findByOwner(String owner, boolean isAdmin, Pageable pageable);

    /**
     * Find question from database by product ID and owner
     * @param productID ID of product that question belongs
     * @param owner username of customer that own the question
     * @param isAdmin is current logged customer an admin
     * @param pageable a Pageable object
     * @return list of question found or empty list
     */
    List<QuestionDTO> findByProductAndOwner(String productID, String owner, boolean isAdmin, Pageable pageable);

    /**
     * Find all questions from database
     * @param isAdmin is current logged customer an admin
     * @param pageable a Pageable object
     * @return list of question found or empty list
     */
    List<QuestionDTO> findAll(boolean isAdmin, Pageable pageable);

    /**
     * Create a question and save to database
     * @param questionDTO question to create
     * @return question saved to database
     */
    QuestionDTO createQuestion(QuestionDTO questionDTO);

    /**
     * Edit a question in database
     * @param questionDTO new question with update
     * @param owner username of current logged in customer
     * @param isAdmin is current logged customer an admin
     * @return edited question
     */
    QuestionDTO editQuestion(QuestionDTO questionDTO, String owner, boolean isAdmin);

    /**
     * Delete a question in database
     * @param id of question to delete
     * @param owner username of current logged in customer
     * @param isAdmin is current logged customer an admin
     * @return deleted question
     */
    QuestionDTO deleteQuestion(String id, String owner, boolean isAdmin);
}
