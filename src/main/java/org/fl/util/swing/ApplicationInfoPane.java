package org.fl.util.swing;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.text.DefaultCaret;

import org.fl.util.RunningContext;
import org.fl.util.json.JsonUtils;

import com.google.gson.JsonObject;

public class ApplicationInfoPane extends JPanel {

	private static final long serialVersionUID = 1L;

	private RunningContext runningContext ;
	
	private JTextArea infosText ;
	private final JScrollPane scrollInfos ;
	private JCheckBox doIpLookUp ;
	
	public ApplicationInfoPane(RunningContext rc) {
		super() ;
		
		runningContext = rc ;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)) ;
		setBorder(BorderFactory.createLineBorder(Color.BLACK,5,true)) ;
		
		doIpLookUp = new JCheckBox("Do lookup on IP addresses (may be slow)") ;
		doIpLookUp.setSelected(false) ;
		doIpLookUp.addActionListener(new SetLookUpListener());
		
		infosText = new JTextArea() ;
		
		// to always be on the top of the scrollPane
		DefaultCaret caret = (DefaultCaret) infosText.getCaret();
		caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		
		scrollInfos = new JScrollPane(infosText) ;	

		add(doIpLookUp) ;
		add(scrollInfos) ;	
	}
	
	public void setInfos() {
		setInfos(doIpLookUp.isSelected()) ;
	}
	
	private void setInfos(boolean withLookUp) {
		JsonObject infosJson = runningContext.getApplicationInfo(withLookUp) ;		
		infosText.setText(JsonUtils.jsonPrettyPrint(infosJson)) ;
		
		scrollInfos.getVerticalScrollBar().setValue(0);
		JViewport vp = scrollInfos.getViewport() ;
		vp.setViewPosition(new Point(0,0));
	}
	
	private class SetLookUpListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (doIpLookUp.isSelected()) {
				infosText.setText("Updating...");
				setInfos(true) ;
			}
		}
		
	}
}
