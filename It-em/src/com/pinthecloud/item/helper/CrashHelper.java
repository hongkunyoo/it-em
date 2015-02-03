package com.pinthecloud.item.helper;

import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.event.ItException;
import com.pinthecloud.item.model.CrashLog;

import de.greenrobot.event.EventBus;

public class CrashHelper implements ReportSender {

	private ItApplication mApp;
	private MobileServiceClient mClient;
	private MobileServiceTable<CrashLog> table;

	public CrashHelper(ItApplication app) {
		this.mApp = app;
		this.mClient = app.getMobileClient();
		this.table = mClient.getTable(CrashLog.class);
	}

	public void setMobileClient(MobileServiceClient client) {
		this.mClient = client;
		this.table = mClient.getTable(CrashLog.class);
	}

	@Override
	public void send(CrashReportData report) throws ReportSenderException {
		String log = report.getProperty(ReportField.LOGCAT);
		String androidVersion = report.getProperty(ReportField.ANDROID_VERSION);
		String versionCode = report.getProperty(ReportField.APP_VERSION_CODE);
		String versionName = report.getProperty(ReportField.APP_VERSION_NAME);
		String brand = report.getProperty(ReportField.BRAND);
		String model = report.getProperty(ReportField.PHONE_MODEL);
		String startDate = report.getProperty(ReportField.USER_APP_START_DATE);

		add(new CrashLog(log, androidVersion, versionCode, versionName, brand, model, startDate));
	}

	public void add(CrashLog log) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("add", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		table.insert(log, new TableOperationCallback<CrashLog>() {

			@Override
			public void onCompleted(CrashLog entity, Exception exception,
					ServiceFilterResponse response) {
				if (exception != null) {
					EventBus.getDefault().post(new ItException("add", ItException.TYPE.SERVER_ERROR, exception));
				}
			}
		});
	}
}
