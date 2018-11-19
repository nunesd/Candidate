package br.edu.ulbra.election.candidate.client;

//public class VoteClientService {
//
//}



import br.edu.ulbra.election.candidate.output.v1.PartyOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class VoteClientService {

    private final VoteClient voteClient;

    @Autowired
    public VoteClientService(VoteClient partyClient){
        this.voteClient = partyClient;
    }

    public PartyOutput getById(Long id){
        return this.voteClient.getById(id);
    }

    @FeignClient(value="party-service", url="${url.party-service}")
    private interface VoteClient {

        @GetMapping("/v1/party/{partyId}")
        PartyOutput getById(@PathVariable(name = "partyId") Long partyId);
    }
}
