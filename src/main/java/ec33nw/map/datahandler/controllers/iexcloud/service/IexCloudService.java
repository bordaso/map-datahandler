package ec33nw.map.datahandler.controllers.iexcloud.service;

import java.util.List;

import org.springframework.stereotype.Service;

import pl.zankowski.iextrading4j.api.refdata.v1.ExchangeSymbol;
import pl.zankowski.iextrading4j.api.stocks.v1.BalanceSheets;
import pl.zankowski.iextrading4j.api.stocks.v1.CashFlows;
import pl.zankowski.iextrading4j.api.stocks.v1.IncomeStatements;
import pl.zankowski.iextrading4j.client.IEXCloudClient;
import pl.zankowski.iextrading4j.client.IEXCloudTokenBuilder;
import pl.zankowski.iextrading4j.client.IEXTradingApiVersion;
import pl.zankowski.iextrading4j.client.IEXTradingClient;
import pl.zankowski.iextrading4j.client.rest.request.refdata.v1.SymbolsRequestBuilder;
import pl.zankowski.iextrading4j.client.rest.request.stocks.PeersRequestBuilder;
import pl.zankowski.iextrading4j.client.rest.request.stocks.v1.BalanceSheetRequestBuilder;
import pl.zankowski.iextrading4j.client.rest.request.stocks.v1.CashFlowRequestBuilder;
import pl.zankowski.iextrading4j.client.rest.request.stocks.v1.IncomeStatementRequestBuilder;

@Service
public class IexCloudService {

	private static final String SANDBOX_TOKEN = "Tpk_2f56411b27df448f80fe029e05d3e67e";
	private static final IEXCloudClient cloudClient = IEXTradingClient.create(
			IEXTradingApiVersion.IEX_CLOUD_STABLE_SANDBOX,
			new IEXCloudTokenBuilder().withPublishableToken(SANDBOX_TOKEN).build());

	public List<ExchangeSymbol> getSymbols() {
		final List<ExchangeSymbol> symbols = cloudClient.executeRequest(new SymbolsRequestBuilder().build());
		return symbols.subList(0, 101);
	}

	public BalanceSheets getBalanceSheets(String symbol) {
		final BalanceSheets balanceSheets = cloudClient
				.executeRequest(new BalanceSheetRequestBuilder().withSymbol(symbol).build());
		return balanceSheets;
	}

	public IncomeStatements getIncomeStatements(String symbol) {
		final IncomeStatements incomeStatements = cloudClient
				.executeRequest(new IncomeStatementRequestBuilder().withSymbol(symbol).build());
		return incomeStatements;
	}

	public CashFlows getCashflowStatements(String symbol) {
		final CashFlows cashFlows = cloudClient.executeRequest(new CashFlowRequestBuilder().withSymbol(symbol).build());
		return cashFlows;
	}

	public List<String> getPeers(String symbol) {
		final List<String> peers = cloudClient.executeRequest(new PeersRequestBuilder().withSymbol(symbol).build());
		return peers;
	}

}
