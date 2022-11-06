package ec33nw.map.datahandler.controllers.iexcloud;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import ec33nw.map.datahandler.SpringContext;
import ec33nw.map.datahandler.controllers.iexcloud.service.IexCloudService;
import ec33nw.map.datahandler.util.GrpcRestDtoParser;
import iexcloud.gen.BalanceSheetGrpc;
import iexcloud.gen.BalanceSheetsGrpc;
import iexcloud.gen.CashflowStatementGrpc;
import iexcloud.gen.CashflowStatementsGrpc;
import iexcloud.gen.IexcloudServiceGrpc;
import iexcloud.gen.IncomeStatementGrpc;
import iexcloud.gen.IncomeStatementsGrpc;
import iexcloud.gen.Symbol;
import pl.zankowski.iextrading4j.api.refdata.v1.ExchangeSymbol;
import pl.zankowski.iextrading4j.api.stocks.v1.BalanceSheet;
import pl.zankowski.iextrading4j.api.stocks.v1.CashFlow;
import pl.zankowski.iextrading4j.api.stocks.v1.IncomeStatement;
import pl.zankowski.iextrading4j.api.stocks.v1.Report;

public class IexCloudGrpcService extends IexcloudServiceGrpc.IexcloudServiceImplBase {

	@Override
	public void getSymbols(iexcloud.gen.NoParam request,
			io.grpc.stub.StreamObserver<iexcloud.gen.Symbol> responseObserver) {		
		List<ExchangeSymbol> symbols = SpringContext.getBean(IexCloudService.class).getSymbols();
		for(ExchangeSymbol symbol : symbols){
			responseObserver.onNext(Symbol.newBuilder().setName(symbol.getSymbol()).build());
			System.out.println("symbol response______" + symbol.toString());
		}
		responseObserver.onCompleted();	
	}

	@Override
	public void getBalancesheets(iexcloud.gen.Symbol request,
			io.grpc.stub.StreamObserver<iexcloud.gen.BalanceSheetsGrpc> responseObserver) {		
		
		List<BalanceSheetGrpc> statements = getStatements(SpringContext.getBean(IexCloudService.class).getBalanceSheets(request.getName()).getBalanceSheet(),
				BalanceSheetGrpc.class, BalanceSheet.class, BalanceSheetGrpc.newBuilder());			
		BalanceSheetsGrpc response = BalanceSheetsGrpc.newBuilder().addAllBalanceSheetGrpc(statements).build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();				
		System.out.println("balance sheet response______" + response.toString());
	}

	@Override
	public void getIncomeStatements(iexcloud.gen.Symbol request,
			io.grpc.stub.StreamObserver<iexcloud.gen.IncomeStatementsGrpc> responseObserver) {
		
		List<IncomeStatementGrpc> statements = getStatements(SpringContext.getBean(IexCloudService.class).getIncomeStatements(request.getName()).getIncome(),
				IncomeStatementGrpc.class, IncomeStatement.class, IncomeStatementGrpc.newBuilder());			
		IncomeStatementsGrpc response = IncomeStatementsGrpc.newBuilder().addAllIncomeStatementGrpc(statements).build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();				
		System.out.println("income response______" + response.toString());
	}

	@Override
	public void getCashflowStatements(iexcloud.gen.Symbol request,
			io.grpc.stub.StreamObserver<iexcloud.gen.CashflowStatementsGrpc> responseObserver) {
		
		List<CashflowStatementGrpc> statements = getStatements(SpringContext.getBean(IexCloudService.class).getCashflowStatements(request.getName()).getCashFlow(),
				CashflowStatementGrpc.class, CashFlow.class, CashflowStatementGrpc.newBuilder());			
		CashflowStatementsGrpc response = CashflowStatementsGrpc.newBuilder().addAllCashflowStatementGrpc(statements).build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();				
		System.out.println("cash flow response______" + response.toString());
	}

	@Override
	public void getPeers(iexcloud.gen.Symbol request,
			io.grpc.stub.StreamObserver<iexcloud.gen.Symbol> responseObserver) {
		List<String> peers = SpringContext.getBean(IexCloudService.class).getPeers(request.getName());
		for(String peerSymbol : peers){
			responseObserver.onNext(Symbol.newBuilder().setName(peerSymbol).build());
			System.out.println("peerSymbol response______" + peerSymbol);
		}
		responseObserver.onCompleted();	
	}
	
	private <GRPCTYP extends com.google.protobuf.GeneratedMessageV3, BLDR extends com.google.protobuf.GeneratedMessageV3.Builder<?>, RESTTYP extends Report>  
	List<GRPCTYP> getStatements(List<RESTTYP> restStmts, Class<?> grpcClass, Class<?> restClass, BLDR builderObj) {

		try {
			List<GRPCTYP> grpcStmts = new ArrayList<>();				
			for(RESTTYP restStmt : restStmts) {
				@SuppressWarnings("unchecked")
				GRPCTYP grpcStmt = 
				(GRPCTYP) GrpcRestDtoParser.INSTANCE.parseRestToGrpc(grpcClass, restClass,
						builderObj, restStmt).build();					
				grpcStmts.add(grpcStmt);
			}				
			return grpcStmts;
		} catch (IllegalAccessException e) {
			System.err.println(e.getClass().toString() + "______" + e.getMessage());
		} catch (IllegalArgumentException e) {
			System.err.println(e.getClass().toString() + "______" + e.getMessage());
		} catch (InvocationTargetException e) {
			System.err.println(e.getClass().toString() + "______" + e.getMessage());
		} catch (NoSuchMethodException e) {
			System.err.println(e.getClass().toString() + "______" + e.getMessage());
		} catch (SecurityException e) {
			System.err.println(e.getClass().toString() + "______" + e.getMessage());
		}			
		return null;
	}
}