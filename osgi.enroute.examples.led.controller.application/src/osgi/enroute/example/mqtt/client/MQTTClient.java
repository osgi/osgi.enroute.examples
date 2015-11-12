package osgi.enroute.example.mqtt.client;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.Listener;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

/**
 * MQTT client to communicate with the MQTT Brokers for Pub/Sub Operation
 */
public final class MQTTClient {

	// List of channels we're subscribed to
	protected Map<String, MessageListener> channels = null;
	// Client
	protected CallbackConnection connection = null;

	/**
	 * Creates a simple MQTT client and connects it to the specified MQTT broker
	 *
	 * @param host
	 *            the hostname of the broker we are trying to connect to
	 */
	public MQTTClient(final String host) {
		this(host, null);
	}

	/**
	 * Creates a simple MQTT client and connects it to the specified MQTT broker
	 *
	 * @param host
	 *            the hostname of the broker
	 * @param clientId
	 *            the UNIQUE id of this client
	 */
	public MQTTClient(final String host, final String clientId) {
		// Create fusesource MQTT client
		final MQTT mqtt = new MQTT();
		try {
			mqtt.setHost(this.hostToURI(host));
			mqtt.setClientId(clientId);
		} catch (final URISyntaxException e) {
			System.out.println("Are you sure you specified host correctly? Terminating...");
		}
		// Initialize channels
		this.channels = new HashMap<>();
		// Register callbacks
		this.connection = mqtt.callbackConnection();
		this.connection.listener(new Listener() {
			@Override
			public void onConnected() {
			}

			@Override
			public void onDisconnected() {
			}

			@Override
			public void onFailure(final Throwable throwable) {
			}

			@Override
			public void onPublish(final UTF8Buffer mqttChannel, final Buffer mqttMessage, final Runnable ack) {
				if (MQTTClient.this.channels.containsKey(mqttChannel.toString())) {
					try {
						MQTTClient.this.channels.get(mqttChannel.toString())
								.processMessage(new String(mqttMessage.toByteArray(), "UTF-8"));
					} catch (final UnsupportedEncodingException e) {
						// yes, we are swallowing...
						System.exit(1);
					}
				}
				ack.run();
			}
		});
		// Connect to broker in a blocking fashion
		final CountDownLatch l = new CountDownLatch(1);
		this.connection.connect(new Callback<Void>() {
			@Override
			public void onFailure(final Throwable throwable) {
				System.err.println(
						"Impossible to CONNECT to the MQTT server! This MQTT client is now useless, create a new one");
			}

			@Override
			public void onSuccess(final Void aVoid) {
				l.countDown();
			}
		});
		try {
			if (!l.await(5, TimeUnit.SECONDS)) {
				// Waits 3 seconds and then timeouts
				System.err.println(
						"Impossible to CONNECT to the MQTT server: TIMEOUT. This MQTT client is now useless, create a new one");
			}
		} catch (final InterruptedException e) {
			System.err
					.println("Impossible to CONNECT to the MQTT server. This MQTT client is useless, create a new one");
		}
	}

	/**
	 * Disconnects the client.
	 */
	public void disconnect() {
		if (this.connection != null) {
			this.connection.disconnect(new Callback<Void>() {
				@Override
				public void onFailure(final Throwable throwable) {
				}

				@Override
				public void onSuccess(final Void aVoid) {
				}
			});
		}
	}

	/**
	 * Returns the channels the client is currently subscribed to.
	 *
	 * @return set of channels the client is currently subscribed to
	 */
	public Set<String> getSubscribedChannels() {
		return this.channels.keySet();
	}

	private String hostToURI(final String host) {
		return "tcp://" + host + ":1883";
	}

	/**
	 * Publish a message to a channel
	 *
	 * @param channel
	 *            the channel we are publishing to
	 * @param message
	 *            the message we are publishing
	 */
	public void publish(final String channel, final String message) {
		if (this.connection != null) {
			this.connection.publish(channel, message.getBytes(), QoS.AT_MOST_ONCE, false, new Callback<Void>() {
				@Override
				public void onFailure(final Throwable throwable) {
					System.out.println("Impossible to publish message to channel " + channel);
				}

				@Override
				public void onSuccess(final Void aVoid) {
				}
			});
		}
	}

	/**
	 * Subscribes to a channel and registers a callback that is fired every time
	 * a new message is published on the channel.
	 *
	 * @param channel
	 *            the channel we are subscribing to
	 * @param callback
	 *            the callback to be fired whenever a message is received on
	 *            this channel
	 */
	public void subscribe(final String channel, final MessageListener callback) {
		if (this.connection != null) {
			if (this.channels.containsKey(channel)) {
				return;
			}
			final CountDownLatch l = new CountDownLatch(1);
			final Topic[] topic = { new Topic(channel, QoS.AT_MOST_ONCE) };
			this.connection.subscribe(topic, new Callback<byte[]>() {
				@Override
				public void onFailure(final Throwable throwable) {
					System.err.println("Impossible to SUBSCRIBE to channel \"" + channel + "\"");
					l.countDown();
				}

				@Override
				public void onSuccess(final byte[] bytes) {
					MQTTClient.this.channels.put(channel, callback);
					l.countDown();
				}
			});
			try {
				l.await();
			} catch (final InterruptedException e) {
				System.err.println("Impossible to SUBSCRIBE to channel \"" + channel + "\"");
			}
		}
	}

	/**
	 * Unsubscribes from a channel.
	 *
	 * @param channel
	 *            the channel we are unsubscribing to
	 */
	public void unsubscribe(final String channel) {
		if (this.connection != null) {
			this.channels.remove(channel);
			final UTF8Buffer[] topic = { UTF8Buffer.utf8(channel) };
			this.connection.unsubscribe(topic, new Callback<Void>() {
				@Override
				public void onFailure(final Throwable throwable) {
				}

				@Override
				public void onSuccess(final Void aVoid) {
				}
			});
		}
	}

}
