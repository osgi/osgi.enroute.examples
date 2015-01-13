package osgi.enroute.examples.scheduler.examples;

import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;

import org.osgi.util.function.Function;
import org.osgi.util.function.Predicate;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Failure;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Success;

import osgi.enroute.scheduler.api.CancelException;
import osgi.enroute.scheduler.api.CancellablePromise;

/*
 * Helper to be able to stop things from the model. We expect a closeable
 * and return a promise. This promise never gets resolved but it can be
 * cancelled. When cancelled, we close the Closeable. This stops the
 * schedule or whatever the closeable does.
 */

class Closer<T> implements CancellablePromise<T> {
	private final Deferred<T> deferred = new Deferred<>();

	Closer(Closeable closeable) {
		deferred.getPromise().onResolve(() -> {
			try {
				closeable.close();
			} catch (Exception e) {
				// ignore
			}
		});
	}

	public boolean isDone() {
		return deferred.getPromise().isDone();
	}

	public T getValue() throws InvocationTargetException, InterruptedException {
		return deferred.getPromise().getValue();
	}

	public Throwable getFailure() throws InterruptedException {
		return deferred.getPromise().getFailure();
	}

	public Promise<T> onResolve(Runnable callback) {
		return deferred.getPromise().onResolve(callback);
	}

	public <R> Promise<R> then(Success<? super T, ? extends R> success,
			Failure failure) {
		return deferred.getPromise().then(success, failure);
	}

	public <R> Promise<R> then(Success<? super T, ? extends R> success) {
		return deferred.getPromise().then(success);
	}

	public Promise<T> filter(Predicate<? super T> predicate) {
		return deferred.getPromise().filter(predicate);
	}

	public <R> Promise<R> map(Function<? super T, ? extends R> mapper) {
		return deferred.getPromise().map(mapper);
	}

	public <R> Promise<R> flatMap(
			Function<? super T, Promise<? extends R>> mapper) {
		return deferred.getPromise().flatMap(mapper);
	}

	public Promise<T> recover(Function<Promise<?>, ? extends T> recovery) {
		return deferred.getPromise().recover(recovery);
	}

	public Promise<T> recoverWith(
			Function<Promise<?>, Promise<? extends T>> recovery) {
		return deferred.getPromise().recoverWith(recovery);
	}

	public Promise<T> fallbackTo(Promise<? extends T> fallback) {
		return deferred.getPromise().fallbackTo(fallback);
	}

	@Override
	public boolean cancel() {
		try {
			deferred.fail(CancelException.SINGLETON);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
