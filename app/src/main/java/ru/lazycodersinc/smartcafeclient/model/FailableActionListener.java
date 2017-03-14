package ru.lazycodersinc.smartcafeclient.model;

/**
 * Created by bob on 14.03.17.
 */
public interface FailableActionListener
{
	public void onSuccess(Object... params);
	public void onError(Object... params);
}
