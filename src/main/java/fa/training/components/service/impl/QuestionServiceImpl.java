package fa.training.components.service.impl;

import fa.training.components.dto.QuestionDTO;
import fa.training.components.entity.Product;
import fa.training.components.entity.Question;
import fa.training.components.exception.MyException;
import fa.training.components.repository.ProductRepository;
import fa.training.components.repository.QuestionRepository;
import fa.training.components.service.QuestionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ProductRepository productRepository;

    @Override
    public QuestionDTO findByID(String id, boolean isAdmin) {
        Optional<Question> question;
        if (isAdmin) {
            question = questionRepository.findById(id);
        } else {
            question = questionRepository.findByIdAndIsDeleted(id, false);
        }
        if (!question.isPresent()) {
            throw new MyException("400", "Question not found");
        }
        Question questionFound = question.get();
        if (!isAdmin) {
            questionFound.setIsDeleted(null);
        }
        return modelMapper.map(questionFound, QuestionDTO.class);
    }

    @Override
    public List<QuestionDTO> findByProduct(String productID, boolean isAdmin, Pageable pageable) {
        List<Question> questions;
        if (isAdmin) {
            questions = questionRepository.findByProductID(productID, pageable);
        } else {
            questions = questionRepository.findByProductIDAndIsDeleted(productID, false, pageable);
        }
        return questions.stream().map(question -> {
            QuestionDTO questionDTO = modelMapper.map(question, QuestionDTO.class);
            if (!isAdmin) {
                questionDTO.setIsDeleted(null);
            }
            return questionDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<QuestionDTO> findByOwner(String owner, boolean isAdmin, Pageable pageable) {
        List<Question> questions;
        if (isAdmin) {
            questions = questionRepository.findByCreatedBy(owner, pageable);
        } else {
            questions = questionRepository.findByCreatedByAndIsDeleted(owner, false, pageable);
        }
        return questions.stream().map(question -> {
            QuestionDTO questionDTO = modelMapper.map(question, QuestionDTO.class);
            if (!isAdmin) {
                questionDTO.setIsDeleted(null);
            }
            return questionDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<QuestionDTO> findByProductAndOwner(String productID, String owner, boolean isAdmin, Pageable pageable) {
        List<Question> questions;
        if (isAdmin) {
            questions = questionRepository.findByProductIDAndCreatedBy(productID, owner, pageable);
        } else {
            questions = questionRepository.findByProductIDAndCreatedByAndIsDeleted(productID, owner, false, pageable);
        }
        return questions.stream().map(question -> {
            QuestionDTO questionDTO = modelMapper.map(question, QuestionDTO.class);
            if (!isAdmin) {
                questionDTO.setIsDeleted(null);
            }
            return questionDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<QuestionDTO> findAll(boolean isAdmin, Pageable pageable) {
        Page<Question> questions;
        if (isAdmin) {
            questions = questionRepository.findAll(pageable);
        } else {
            questions = questionRepository.findByIsDeleted(false, pageable);
        }
        return questions.stream().map(question -> {
            QuestionDTO questionDTO = modelMapper.map(question, QuestionDTO.class);
            if (!isAdmin) {
                questionDTO.setIsDeleted(null);
            }
            return questionDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public QuestionDTO createQuestion(QuestionDTO questionDTO) {
        // Check product ID
        Optional<Product> product = productRepository.findByIdAndIsDeleted(questionDTO.getProductID(), false);
        if (!product.isPresent()) {
            throw new MyException("400", "Product not found");
        }

        UUID id = UUID.randomUUID();
        questionDTO.setId(id.toString());

        questionDTO.setIsDeleted(false);

        Question savedQuestion = questionRepository.save(modelMapper.map(questionDTO, Question.class));
        savedQuestion.setIsDeleted(null);
        return modelMapper.map(savedQuestion, QuestionDTO.class);
    }

    @Override
    public QuestionDTO editQuestion(QuestionDTO questionDTO, String owner, boolean isAdmin) {
        Optional<Question> question = questionRepository.findByIdAndIsDeleted(questionDTO.getId(), false);
        if (!question.isPresent()) {
            throw new MyException("400", "Question not found");
        }
        Question questionToEdit = question.get();
        if (!isAdmin) {
            if (!owner.equals(questionToEdit.getCreatedBy())) {
                throw new MyException("403", "Just owner or admin can edit question");
            }
        }
        questionToEdit.setContent(questionDTO.getContent());
        Question editedQuestion = questionRepository.saveAndFlush(questionToEdit);
        editedQuestion.setIsDeleted(null);
        return modelMapper.map(editedQuestion, QuestionDTO.class);
    }

    @Override
    public QuestionDTO deleteQuestion(String id, String owner, boolean isAdmin) {
        Optional<Question> questione = questionRepository.findByIdAndIsDeleted(id, false);
        if (!questione.isPresent()) {
            throw new MyException("400", "Question not found");
        }
        Question questionToDelete = questione.get();
        if (!isAdmin) {
            if (!owner.equals(questionToDelete.getCreatedBy())) {
                throw new MyException("403", "Just owner or admin can delete question");
            }
        }
        questionToDelete.setIsDeleted(true);
        Question deletedQuestion = questionRepository.saveAndFlush(questionToDelete);
        deletedQuestion.setIsDeleted(null);
        return modelMapper.map(deletedQuestion, QuestionDTO.class);
    }
}
