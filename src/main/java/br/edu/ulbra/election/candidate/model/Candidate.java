package br.edu.ulbra.election.candidate.model;

import javax.persistence.*;

@Entity
public class Candidate {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private Long partyId;

	@Column(nullable = false)
	private Long numberElection;

	@Column(nullable = false)
	private Long electionId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getNumberElection() {
		return numberElection;
	}

	public void setNumberElection(Long numberElection) {
		this.numberElection = numberElection;
	}

	public Long getElectionId() {
		return electionId;
	}

	public void setElectionId(Long electionid) {
		this.electionId = electionid;
	}

	public Long getPartyId() {
		return partyId;
	}

	public void setPartyId(Long partyid) {
		this.partyId = partyid;
	}

}
