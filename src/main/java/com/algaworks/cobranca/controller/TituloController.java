package com.algaworks.cobranca.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.cobranca.model.StatusTitulo;
import com.algaworks.cobranca.model.Titulo;
import com.algaworks.cobranca.repository.Titulos;
import com.algaworks.cobranca.repository.filter.TituloFielter;
import com.algaworks.cobranca.service.CadastroTituloService;

@Controller
@RequestMapping("/titulos")
public class TituloController {
	
	private static final String CADASTRO_VIEW = "CadastroTitulo";
	
	@Autowired
	private CadastroTituloService cadastroTituloService;
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView pesquisar(@ModelAttribute("filtro") TituloFielter filtro) {
		List<Titulo> todosTitulos = cadastroTituloService.filtrar(filtro);
		ModelAndView mv = new ModelAndView("PesquisaTitulo");
		mv.addObject("titulos", todosTitulos);
		return mv;
	}
	
	
	@RequestMapping(value = "/novo", method = RequestMethod.GET)
	public ModelAndView cadastrar() {
		ModelAndView mv = new ModelAndView(CADASTRO_VIEW);
		mv.addObject("titulo", new Titulo());
		return mv;
	}
	
	@RequestMapping(value = "/novo", method = RequestMethod.POST)
	public String salvar(@Validated Titulo titulo, Errors errors, RedirectAttributes attributes) {
		if(errors.hasErrors()) {
			return CADASTRO_VIEW;
		}
		try {
			cadastroTituloService.salvar(titulo);
			attributes.addFlashAttribute("mensagem", "Título salvo com sucesso!");
			return "redirect:/titulos/novo";
		} catch (IllegalArgumentException e) {
			errors.rejectValue("dataVencimento", null, e.getMessage());
			return CADASTRO_VIEW;
		}
	}
	
	@RequestMapping("{codigo}")
	public ModelAndView edicao (@PathVariable("codigo") Titulo titulo) {
		ModelAndView mv = new ModelAndView(CADASTRO_VIEW);
		mv.addObject("titulo", titulo);
		return mv;
	}
	
	@RequestMapping(method = RequestMethod.DELETE, value="{codigo}")
	public ModelAndView excluir (@PathVariable ("codigo") Titulo titulo, RedirectAttributes attributes) {
		cadastroTituloService.excluir(titulo);		
		attributes.addFlashAttribute("mensagem", "Título excluído com sucesso!");
		ModelAndView mv = new ModelAndView("redirect:/titulos");
		return mv;
	}
	
	@ResponseBody
	@RequestMapping(value = "/{codigo}/receber", method = RequestMethod.PUT)
	public String receber(@PathVariable ("codigo") Titulo titulo) {
		return cadastroTituloService.receber(titulo);
	}

	@ModelAttribute("statusTitulo")
	public List<StatusTitulo> getStatusTitulo(){
		return Arrays.asList(StatusTitulo.values());
	}

}
