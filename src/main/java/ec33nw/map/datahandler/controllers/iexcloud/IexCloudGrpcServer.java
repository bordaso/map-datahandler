package ec33nw.map.datahandler.controllers.iexcloud;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
import io.grpc.Server;
import io.grpc.ServerBuilder;
import pl.zankowski.iextrading4j.api.refdata.v1.ExchangeSymbol;
import pl.zankowski.iextrading4j.api.stocks.v1.BalanceSheet;
import pl.zankowski.iextrading4j.api.stocks.v1.CashFlow;
import pl.zankowski.iextrading4j.api.stocks.v1.IncomeStatement;
import pl.zankowski.iextrading4j.api.stocks.v1.Report;

public class IexCloudGrpcServer {

	private final int port;
	private final Server server;
	private static final IexCloudService icService = new IexCloudService();

	public IexCloudGrpcServer(int port) throws IOException {
		this(ServerBuilder.forPort(port), port);
	}

	public IexCloudGrpcServer(ServerBuilder<?> serverBuilder, int port) {
		this.port = port;
		server = serverBuilder.addService(new IexCloudGrpcService()).build();
	}
	
	  /** Start serving requests. */
	  public void start() throws IOException {
	    server.start();
	    System.out.println("GRPC Server started, listening on " + port);
	    Runtime.getRuntime().addShutdownHook(new Thread() {
	      @Override
	      public void run() {
	        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
	        System.err.println("*** shutting down gRPC server since JVM is shutting down");
	        try {
	        	IexCloudGrpcServer.this.stop();
	        } catch (InterruptedException e) {
	          e.printStackTrace(System.err);
	        }
	        System.err.println("*** server shut down");
	      }
	    });
	  }

	  /** Stop serving requests and shutdown resources. */
	  public void stop() throws InterruptedException {
	    if (server != null) {
	      server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
	    }
	  }

	  /**
	   * Await termination on the main thread since the grpc library uses daemon threads.
	   */
	  public void blockUntilShutdown() throws InterruptedException {
	    if (server != null) {
	      server.awaitTermination();
	    }
	  }


	private class IexCloudGrpcService extends IexcloudServiceGrpc.IexcloudServiceImplBase {

		@Override
		public void getSymbols(iexcloud.gen.NoParam request,
				io.grpc.stub.StreamObserver<iexcloud.gen.Symbol> responseObserver) {
			List<ExchangeSymbol> symbols = icService.getSymbols();
			for(ExchangeSymbol symbol : symbols){
				responseObserver.onNext(Symbol.newBuilder().setName(symbol.getSymbol()).build());
				System.out.println("symbol response______" + symbol.toString());
			}
			responseObserver.onCompleted();	
		}

		@Override
		public void getBalancesheets(iexcloud.gen.Symbol request,
				io.grpc.stub.StreamObserver<iexcloud.gen.BalanceSheetsGrpc> responseObserver) {		
			
			List<BalanceSheetGrpc> statements = getStatements(icService.getBalanceSheets(request.getName()).getBalanceSheet(),
					BalanceSheetGrpc.class, BalanceSheet.class, BalanceSheetGrpc.newBuilder());			
			BalanceSheetsGrpc response = BalanceSheetsGrpc.newBuilder().addAllBalanceSheetGrpc(statements).build();
			responseObserver.onNext(response);
			responseObserver.onCompleted();				
			System.out.println("balance sheet response______" + response.toString());
		}

		@Override
		public void getIncomeStatements(iexcloud.gen.Symbol request,
				io.grpc.stub.StreamObserver<iexcloud.gen.IncomeStatementsGrpc> responseObserver) {
			
			List<IncomeStatementGrpc> statements = getStatements(icService.getIncomeStatements(request.getName()).getIncome(),
					IncomeStatementGrpc.class, IncomeStatement.class, IncomeStatementGrpc.newBuilder());			
			IncomeStatementsGrpc response = IncomeStatementsGrpc.newBuilder().addAllIncomeStatementGrpc(statements).build();
			responseObserver.onNext(response);
			responseObserver.onCompleted();				
			System.out.println("income response______" + response.toString());
		}

		@Override
		public void getCashflowStatements(iexcloud.gen.Symbol request,
				io.grpc.stub.StreamObserver<iexcloud.gen.CashflowStatementsGrpc> responseObserver) {
			
			List<CashflowStatementGrpc> statements = getStatements(icService.getCashflowStatements(request.getName()).getCashFlow(),
					CashflowStatementGrpc.class, CashFlow.class, CashflowStatementGrpc.newBuilder());			
			CashflowStatementsGrpc response = CashflowStatementsGrpc.newBuilder().addAllCashflowStatementGrpc(statements).build();
			responseObserver.onNext(response);
			responseObserver.onCompleted();				
			System.out.println("cash flow response______" + response.toString());
		}

		@Override
		public void getPeers(iexcloud.gen.Symbol request,
				io.grpc.stub.StreamObserver<iexcloud.gen.Symbol> responseObserver) {
			List<String> peers = icService.getPeers(request.getName());
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

}
