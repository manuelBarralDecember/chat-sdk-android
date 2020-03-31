package co.chatsdk.xmpp;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import org.jivesoftware.smackx.mam.MamManager;
import org.jivesoftware.smackx.mam.element.MamElements;
import org.jxmpp.jid.impl.JidCreate;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.utils.DisposableList;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class XMPPMamManager {

    private WeakReference<XMPPManager> manager;

    public XMPPMamManager(XMPPManager manager){
        this.manager = new WeakReference<>(manager);
    }

    public boolean onlyAllowMessagesForCurrentDomain = false;

    public Single<List<Message>> getMessageArchive(String from, int max) {
        return Single.create((SingleOnSubscribe<List<Message>>) emitter -> {
            try {
                List<Message> messages = new ArrayList<>();
                if(manager.get().mamManager().isSupported()) {
                    MamManager.MamQuery query = manager.get().mamManager().queryMostRecentPage(JidCreate.bareFrom(from), max);
                    messages.addAll(query.getMessages());
                    List<MamElements.MamResultExtension> extensions = query.getMamResultExtensions();
                    for (int i = 0; i <messages.size(); i++) {
                        Message m = messages.get(i);
                        if (!onlyAllowMessagesForCurrentDomain || currentDomain(m)) {
                            MamElements.MamResultExtension extension = extensions.get(i);
                            DelayInformation delay = extension.getForwarded().getDelayInformation();
                            m.addExtension(delay);
                        }
                    }
                }
                emitter.onSuccess(messages);
            } catch (Exception e) {
                emitter.onError(e);
            }
        }).subscribeOn(Schedulers.io());
    }

    protected boolean currentDomain (Message m) {
        return m.getFrom().getDomain().equals(manager.get().getConnection().getXMPPServiceDomain());
    }

}
