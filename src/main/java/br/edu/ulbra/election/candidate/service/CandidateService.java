package br.edu.ulbra.election.candidate.service;

import br.edu.ulbra.election.candidate.repository.CandidateRepository;
import feign.FeignException;
import br.edu.ulbra.election.candidate.client.ElectionClientService;
import br.edu.ulbra.election.candidate.client.PartyClientService;
import br.edu.ulbra.election.candidate.exception.GenericOutputException;
import br.edu.ulbra.election.candidate.input.v1.CandidateInput;
import br.edu.ulbra.election.candidate.model.Candidate;
import br.edu.ulbra.election.candidate.output.v1.CandidateOutput;
import br.edu.ulbra.election.candidate.output.v1.GenericOutput;
import br.edu.ulbra.election.candidate.output.v1.ElectionOutput;
import br.edu.ulbra.election.candidate.output.v1.PartyOutput;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CandidateService {

	private final CandidateRepository candidateRepository;
	private final ElectionClientService electionClientService;
	private final PartyClientService partyClientService;
	private final ModelMapper modelMapper;

	private static final String MESSAGE_INVALID_ID = "Invalid id";
	private static final String MESSAGE_INVALID_ELECTION_ID = "Invalid Election Id";
	private static final String MESSAGE_CANDIDATE_NOT_FOUND = "Candidate not found";

	@Autowired
	public CandidateService(CandidateRepository candidateRepository, ModelMapper modelMapper, ElectionClientService electionClientService, PartyClientService partyClientService) {
		this.candidateRepository = candidateRepository;
		this.modelMapper = modelMapper;
		this.electionClientService = electionClientService;
		this.partyClientService = partyClientService;
	}

	public List<CandidateOutput> getAll() {
		List<Candidate> candidateList = (List<Candidate>) candidateRepository.findAll();
		return candidateList.stream().map(this::takeCandidateOutput).collect(Collectors.toList());
	}

	public CandidateOutput create(CandidateInput candidateInput) {
		validateInput(candidateInput);
		validateDuplicate(candidateInput, null);
		Candidate candidate = modelMapper.map(candidateInput, Candidate.class);
		candidate = candidateRepository.save(candidate);
		return takeCandidateOutput(candidate);
	}

	public CandidateOutput getById(Long candidateId) {
		if (candidateId == null) {
			throw new GenericOutputException(MESSAGE_INVALID_ID);
		}

		Candidate candidate = candidateRepository.findById(candidateId).orElse(null);
		if (candidate == null) {
			throw new GenericOutputException(MESSAGE_CANDIDATE_NOT_FOUND);
		}

		return takeCandidateOutput(candidate);
	}

	public CandidateOutput update(Long candidateId, CandidateInput candidateInput) {
		if (candidateId == null) {
			throw new GenericOutputException(MESSAGE_INVALID_ID);
		}

		verifyVotes(candidateId);
		validateInput(candidateInput);
		validateDuplicate(candidateInput, candidateId);

		Candidate candidate = candidateRepository.findById(candidateId).orElse(null);
		if (candidate == null) {
			throw new GenericOutputException(MESSAGE_CANDIDATE_NOT_FOUND);
		}

		candidate.setName(candidateInput.getName());
		candidate.setNumberElection(candidateInput.getNumberElection());
		candidate.setPartyId(candidateInput.getPartyId());
		candidate.setElectionId(candidateInput.getElectionId());
		candidate = candidateRepository.save(candidate);

		return takeCandidateOutput(candidate);
	}

	public GenericOutput delete(Long candidateId) {
		if (candidateId == null) {
			throw new GenericOutputException(MESSAGE_INVALID_ID);
		}

		verifyVotes(candidateId);

		Candidate candidate = candidateRepository.findById(candidateId).orElse(null);
		if (candidate == null) {
			throw new GenericOutputException(MESSAGE_CANDIDATE_NOT_FOUND);
		}

		candidateRepository.delete(candidate);

		return new GenericOutput("Candidate deleted");
	}

	private void validateInput(CandidateInput candidateInput) {
		if (StringUtils.isBlank(candidateInput.getName())
				|| candidateInput.getName().trim().replace(" ", "").length() < 5) {
			throw new GenericOutputException("Invalid name");
		} else {
			String name[] = candidateInput.getName().split(" ");
			if (name.length < 2) {
				throw new GenericOutputException("Invalid name");
			}
		}
		if (candidateInput.getNumberElection() == null) {
			throw new GenericOutputException("Invalid number");
		}
		if (candidateInput.getPartyId() == null) {
			throw new GenericOutputException("Invalid party");
		}
		if (candidateInput.getElectionId() == null) {
			throw new GenericOutputException("Invalid election");
		}

		try {
			PartyOutput partyOutput = partyClientService.getById(candidateInput.getPartyId());
			if (!candidateInput.getNumberElection().toString().startsWith(partyOutput.getNumber().toString())) {
				throw new GenericOutputException("Number doesn't belong to party");
			}
		} catch (FeignException e) {
			if (e.status() == 500) {
				throw new GenericOutputException("Invalid Party");
			}
		}

		try {
			electionClientService.getById(candidateInput.getElectionId());
		} catch (FeignException e) {
			if (e.status() == 500) {
				throw new GenericOutputException(MESSAGE_INVALID_ELECTION_ID);
			}
		}
	}

	private void validateDuplicate(CandidateInput candidateInput, Long candidateId) {
		Candidate candidate = candidateRepository.findFirstByNumberElectionAndElectionId(
				candidateInput.getNumberElection(), candidateInput.getElectionId());
		if (candidate != null && candidate.getId() != candidateId) {
			throw new GenericOutputException("This candidate already exist.");
		}
	}

	public CandidateOutput takeCandidateOutput(Candidate candidate) {
		CandidateOutput candidateOutput = modelMapper.map(candidate, CandidateOutput.class);
		ElectionOutput electionOutput = electionClientService.getById(candidate.getElectionId());
		candidateOutput.setElectionOutput(electionOutput);
		PartyOutput partyOutput = partyClientService.getById(candidate.getPartyId());
		candidateOutput.setPartyOutput(partyOutput);
		return candidateOutput;
	}

	public CandidateOutput verifyNumero(Long numberElection, Long electionId) {
		Candidate aux = candidateRepository.findFirstByNumberElectionAndElectionId(numberElection, electionId);
		return takeCandidateOutput(aux);
	}

	public CandidateOutput verifyElection(Long electionId) {
		Candidate aux = candidateRepository.findFirstByElectionId(electionId);
		return takeCandidateOutput(aux);
	}

	public CandidateOutput verifyParty(Long partyId) {
		Candidate aux = candidateRepository.findFirstByPartyId(partyId);
		return takeCandidateOutput(aux);
	}

	private void verifyVotes(Long candidateId) {
		try {
			Long electionId = getById(candidateId).getElectionOutput().getId();
			if (electionClientService.getResultElection(electionId).getTotalVotes() > 0) {
				throw new GenericOutputException("This election already have votes.");
			}
		} catch (FeignException e) {
			throw new GenericOutputException("Error");
		}
	}

	public ArrayList<CandidateOutput> getListCandidatesByElectionId(Long electionId) {

		Iterable<Candidate> listTwo = candidateRepository.findAll();

		ArrayList<CandidateOutput> list = new ArrayList<>();

		for (Candidate aux : listTwo) {
			if (aux.getElectionId().equals(electionId)) {
				list.add(takeCandidateOutput(aux));
			}
		}
		return list;
	}

}
