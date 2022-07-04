package ec33nw.map.datahandler.controllers.iexcloud;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec33nw.map.datahandler.controllers.iexcloud.service.IexCloudService;
import pl.zankowski.iextrading4j.api.refdata.v1.ExchangeSymbol;
import pl.zankowski.iextrading4j.api.stocks.v1.BalanceSheets;
import pl.zankowski.iextrading4j.api.stocks.v1.CashFlows;
import pl.zankowski.iextrading4j.api.stocks.v1.IncomeStatements;

@RestController
@RequestMapping(path = "/api")
public class IexCloudRestController {
	
	@Autowired
	private IexCloudService icService;

	@GetMapping(path = "symbols", produces=MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin(origins = "http://localhost:4200")
	public String getSymbols() {		
		final List<ExchangeSymbol> symbols = icService.getSymbols();
		return symbols.get(0).toString();
	}
	
	@GetMapping(path = "balancesheet", produces=MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin(origins = "http://localhost:4200")
	public String getBalancesheet() {		
		final BalanceSheets balanceSheets = icService.getBalanceSheets("A");
		return balanceSheets.getBalanceSheet().get(0).toString();
	}
	
	@GetMapping(path = "balancesheetact", produces=MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin(origins = "http://localhost:4200")
	public BalanceSheets getBalancesheetAct() {		
		return icService.getBalanceSheets("A");
	}
	
	@GetMapping(path = "income", produces=MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin(origins = "http://localhost:4200")
	public String getIncomeStatement() {		
		final IncomeStatements incomeStatement = icService.getIncomeStatements("A");
		return incomeStatement.getIncome().get(0).toString();
	}
	
	@GetMapping(path = "incomeact", produces=MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin(origins = "http://localhost:4200")
	public IncomeStatements getIncomeStatementAct() {		
		return icService.getIncomeStatements("A");
	}
	
	@GetMapping(path = "cashflow", produces=MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin(origins = "http://localhost:4200")
	public String getCashflowStatement() {		
		final CashFlows cashFlows = icService.getCashflowStatements("A");
		return cashFlows.getCashFlow().get(0).toString();
	}
	
	@GetMapping(path = "cashflowact", produces=MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin(origins = "http://localhost:4200")
	public CashFlows getCashflowStatementAct() {	
		return icService.getCashflowStatements("A");
	}
	
	@GetMapping(path = "peers", produces=MediaType.APPLICATION_JSON_VALUE)
	@CrossOrigin(origins = "http://localhost:4200")	
	public List<String> getPeers() {		
		final List<String> peers = icService.getPeers("A");	
		return peers;
	}
	
}
