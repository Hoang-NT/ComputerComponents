package fa.training.components.service.impl;

import fa.training.components.dto.AnswerDTO;
import fa.training.components.entity.Answer;
import fa.training.components.entity.Question;
import fa.training.components.exception.MyException;
import fa.training.components.repository.AnswerRepository;
import fa.training.components.repository.QuestionRepository;
import fa.training.components.service.AnswerService;
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
public class AnswerServiceImpl implements AnswerService {
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private QuestionRepository questionRepository;

    @Override
    public AnswerDTO findByID(String id, boolean isAdmin) {
        Optional<Answer> answer;
        if (isAdmin) {
            answer = answerRepository.findById(id);
        } else {
            answer = answerRepository.findByIdAndIsDeleted(id, false);
        }
        if (!answer.isPresent()) {
            throw new MyException("400", "Answer not found");
        }
        Answer answerFound = answer.get();
        if (!isAdmin) {
            answerFound.setIsDeleted(null);
        }
        return modelMapper.map(answerFound, AnswerDTO.class);
    }

    @Override
    public List<AnswerDTO> findByQuestion(String questionID, boolean isAdmin, Pageable pageable) {
        List<Answer> answers;
        if (isAdmin) {
            answers = answerRepository.findByQuestionID(questionID, pageable);
        } else {
            answers = answerRepository.findByQuestionIDAndIsDeleted(questionID, false, pageable);
        }
        return answers.stream().map(answer -> {
            AnswerDTO answerDTO = modelMapper.map(answer, AnswerDTO.class);
            if (!isAdmin) {
                answerDTO.setIsDeleted(null);
            }
            return answerDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<AnswerDTO> findByOwner(String owner, boolean isAdmin, Pageable pageable) {
        List<Answer> answers;
        if (isAdmin) {
            answers = answerRepository.findByCreatedBy(owner, pageable);
        } else {
            answers =answerRepository.findByCreatedByAndIsDeleted(owner, false, pageable);
        }
        return answers.stream().map(answer -> {
            AnswerDTO answerDTO = modelMapper.map(answer, AnswerDTO.class);
            if (!isAdmin) {
                answerDTO.setIsDeleted(null);
            }
            return answerDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<AnswerDTO> findAll(boolean isAdmin, Pageable pageable) {
        Page<Answer> answers;
        if (isAdmin) {
            answers = answerRepository.findAll(pageable);
        } else {
            answers = answerRepository.findByIsDeleted(false, pageable);
        }
        return answers.stream().map(answer -> {
            AnswerDTO answerDTO = modelMapper.map(answer, AnswerDTO.class);
            if (!isAdmin) {
                answerDTO.setIsDeleted(null);
            }
            return answerDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public AnswerDTO createAnswer(AnswerDTO answerDTO) {
        // Check question
        Optional<Question> question = questionRepository.findByIdAndIsDeleted(answerDTO.getQuestionID(), false);
        if (!question.isPresent()) {
            throw new MyException("400", "Question not found");
        }

        UUID id = UUID.randomUUID();
        answerDTO.setId(id.toString());

        answerDTO.setIsDeleted(false);

        Answer savedAnswer = answerRepository.save(modelMapper.map(answerDTO, Answer.class));
        savedAnswer.setIsDeleted(null);
        return modelMapper.map(savedAnswer, AnswerDTO.class);
    }

    @Override
    public AnswerDTO editAnswer(AnswerDTO answerDTO, String owner, boolean isAdmin) {
        Optional<Answer> answer = answerRepository.findByIdAndIsDeleted(answerDTO.getId(), false);
        if (!answer.isPresent()) {
            throw new MyException("400", "Answer not found");
        }
        Answer answerToEdit = answer.get();
        if (!isAdmin) {
            if (!owner.equals(answerToEdit.getCreatedBy())) {
                throw new MyException("403", "Just owner or admin can edit answer");
            }
        }
        answerToEdit.setContent(answerDTO.getContent());
        Answer editedAnswer = answerRepository.saveAndFlush(answerToEdit);
        editedAnswer.setIsDeleted(null);
        return modelMapper.map(editedAnswer, AnswerDTO.class);
    }

    @Override
    public AnswerDTO deleteAnswer(String id, String owner, boolean isAdmin) {
        Optional<Answer> answer = answerRepository.findByIdAndIsDeleted(id, false);
        if (!answer.isPresent()) {
            throw new MyException("400", "Answer not found");
        }
        Answer answerToDelete = answer.get();
        if (!isAdmin) {
            if (!owner.equals(answerToDelete.getCreatedBy())) {
                throw new MyException("403", "Just owner or admin can delete answer");
            }
        }
        answerToDelete.setIsDeleted(true);
        Answer deletedAnswer = answerRepository.saveAndFlush(answerToDelete);
        deletedAnswer.setIsDeleted(null);
        return modelMapper.map(deletedAnswer, AnswerDTO.class);
    }
}
