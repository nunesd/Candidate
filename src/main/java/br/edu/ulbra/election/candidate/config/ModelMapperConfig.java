package br.edu.ulbra.election.candidate.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.*;

@Configuration
public class ModelMapperConfig {
	
	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setAmbiguityIgnored(true);
		return modelMapper;
	}
}
