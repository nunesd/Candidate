package br.edu.ulbra.election.candidate.model;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.persistence.*;

import br.edu.ulbra.election.candidate.exception.GenericOutputException;
import br.edu.ulbra.election.candidate.output.v1.ElectionOutput;
import br.edu.ulbra.election.candidate.output.v1.PartyOutput;

@Entity
public class Candidate {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false, name="number")
	private Long numberElection;
	
	@Column(nullable = false)
	private Long electionId;
	
	@Column(nullable = false)
	private Long partyId;

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

	public void setElectionId(Long electionId) {
		this.electionId = electionId;
	}

	public Long getPartyId() {
		return partyId;
	}

	public void setPartyId(Long partyId) {
		this.partyId = partyId;
	}
//	
//	public PartyOutput getPtOut() {
//		PartyOutput ptOut = new PartyOutput();
//		ptOut.setId(this.getPartyId());
//		return ptOut;
//	}
//	
//	public ElectionOutput getElOut() {
//		ElectionOutput elOut = new ElectionOutput();
//		elOut.setId(this.getElectionId());
//		return elOut;
//	}
}