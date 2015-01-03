package zsoltmester.qcn.notifications;

import android.app.Notification;
import android.service.notification.StatusBarNotification;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class NotificationHelper {

	private static Sorter sorter;

	public static void sortNotifications(List<StatusBarNotification> nfs, String[] rm) {
		if (sorter == null) {
			sorter = new Sorter();
		}
		sorter.setRankingMap(rm);

		Collections.sort(nfs, sorter);
	}

	@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
	public static void selectValidNotifications(List<StatusBarNotification> nfs) {
		synchronized (nfs) {
			Iterator i = nfs.iterator();
			while (i.hasNext()) {
				if (!isDisplayable((StatusBarNotification) i.next())) {
					i.remove();
				}
			}
		}
	}

	public static boolean isDisplayable(StatusBarNotification sbn) {
		// TODO it's enough for check if it is displayable?
		return sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE) != null;
	}

	@SuppressWarnings({"unchecked", "SynchronizationOnLocalVariableOrMethodParameter"})
	public static void insertNotification(List<StatusBarNotification> nfs, StatusBarNotification sbn, String[] rm) {
		synchronized (nfs) {
			ListIterator i = nfs.listIterator();
			while (i.hasNext()) {
				StatusBarNotification csbn = (StatusBarNotification) i.next(); // current sbn
				if (csbn.getId() == sbn.getId()) {
					i.set(sbn);
					return;
				}
			}

			nfs.add(getRank(sbn, rm), sbn);
		}

		sortNotifications(nfs, rm);
	}

	@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
	public static void deleteNotification(List<StatusBarNotification> nfs, StatusBarNotification sbn, String[] rm) {
		synchronized (nfs) {
			Iterator i = nfs.iterator();
			while (i.hasNext()) {
				if (((StatusBarNotification) i.next()).getId() == sbn.getId()) {
					i.remove();
					break;
				}
			}
		}

		sortNotifications(nfs, rm);
	}

	private static int getRank(StatusBarNotification sbn, String[] rm) {
		for (int j = 0; j < rm.length; ++j) {
			if (rm[j].equals(sbn.getKey())) {
				return j;
			}
		}

		return rm.length;
	}

	private static class Sorter implements Comparator<StatusBarNotification> {

		private String[] rm;

		private void setRankingMap(String[] rm) {
			this.rm = rm;
		}

		@Override
		public int compare(StatusBarNotification lhs, StatusBarNotification rhs) {
			return getRank(lhs, rm) - getRank(rhs, rm);
		}
	}
}
