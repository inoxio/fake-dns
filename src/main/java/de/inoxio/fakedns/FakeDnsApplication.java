package de.inoxio.fakedns;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import de.inoxio.fakedns.protocol.ServiceHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LogLevel;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@Slf4j
@SpringBootApplication
@ConfigurationPropertiesScan
@RequiredArgsConstructor
public class FakeDnsApplication implements CommandLineRunner {

	private final ApplicationProperties applicationProperties;
	private final ServiceHandler serviceHandler;

	public static void main(final String[] args) {
		SpringApplication.run(FakeDnsApplication.class, args);
	}

	@Override
	public void run(final String... args) throws IOException {
		log.info("Initializing FakeDns...");

		// configure logging filter
		final var loggingFilter = new LoggingFilter();
		loggingFilter.setExceptionCaughtLogLevel(LogLevel.WARN);
		loggingFilter.setMessageReceivedLogLevel(LogLevel.TRACE);
		loggingFilter.setMessageSentLogLevel(LogLevel.TRACE);
		loggingFilter.setSessionClosedLogLevel(LogLevel.TRACE);
		loggingFilter.setSessionCreatedLogLevel(LogLevel.TRACE);
		loggingFilter.setSessionIdleLogLevel(LogLevel.TRACE);
		loggingFilter.setSessionOpenedLogLevel(LogLevel.TRACE);

		// ------- tcp ----------------------------------------------------------------
		log.info("Starting TCP-Listener on port " + applicationProperties.getPort() + "...");
		final var tcpAcceptor = new NioSocketAcceptor();
		tcpAcceptor.getFilterChain().addLast("logger", loggingFilter);
		tcpAcceptor.getFilterChain().addLast("threadPool",
				new ExecutorFilter(Executors.newFixedThreadPool(applicationProperties.getThreadPoolSize())));

		// Set the handler
		tcpAcceptor.setHandler(serviceHandler);

		// Allow the port to be reused even if the socket is in TIME_WAIT state
		tcpAcceptor.setReuseAddress(true);

		// No Nagle's algorithm
		tcpAcceptor.getSessionConfig().setTcpNoDelay(true);

		// port to listen on
		tcpAcceptor.setDefaultLocalAddress(new InetSocketAddress(applicationProperties.getPort()));

		// start the server
		tcpAcceptor.bind();

		// ------- udp ----------------------------------------------------------------
		log.info("Starting UDP-Listener on port " + applicationProperties.getPort() + "...");
		final var udpAcceptor = new NioDatagramAcceptor();
		udpAcceptor.getFilterChain().addLast("logger", loggingFilter);
		udpAcceptor.getFilterChain().addLast("threadPool",
				new ExecutorFilter(Executors.newFixedThreadPool(applicationProperties.getThreadPoolSize())));

		// Set the handler
		udpAcceptor.setHandler(serviceHandler);

		// Allow the port to be reused even if the socket is in TIME_WAIT state
		udpAcceptor.getSessionConfig().setReuseAddress(true);

		// port to listen on
		udpAcceptor.setDefaultLocalAddress(new InetSocketAddress(applicationProperties.getPort()));

		// Set the handler
		udpAcceptor.bind();

	}
}
