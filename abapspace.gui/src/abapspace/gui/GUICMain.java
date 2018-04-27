package abapspace.gui;

import java.io.File;
import java.util.List;

import javax.swing.JOptionPane;

import abapspace.core.exception.FileProcessException;
import abapspace.core.exception.PresetDirNotFoundException;
import abapspace.core.exception.SourceDirectoryNotFoundException;
import abapspace.core.exception.TargetDirectoryNotFoundException;
import abapspace.core.preset.entity.Preset;

public class GUICMain {

	private GUIMMain guimmain;
	private GUIMain guimain;

	public GUICMain() {

		// init GUI
		this.guimain = new GUIMain(this);

		// init model
		this.guimmain = new GUIMMain();

		this.setPresetManager(null);
	}

	public void startGUI() {
		this.guimain.getFrameMain().setVisible(true);
	}

	public void stopGUI() {
		this.guimain.getFrameMain().setVisible(false);
		System.exit(0);
	}

	public void chooseDirPreset() {
		File locPresetDir = this.guimain.showDirectoryChooser();

		if (locPresetDir == null) {
			return;
		}

		this.setPresetManager(locPresetDir);
	}

	public void choosePreset(Preset preset) {
		this.guimmain.setPreset(preset);

		try {
			this.guimain.getPanelMain().getTxtSourceDir().setText(preset.getFileSourceDir().getAbsolutePath());
		} catch (SourceDirectoryNotFoundException e) {
			this.guimain.getPanelMain().getTxtSourceDir().setText(null);
			this.guimain.showMessage(e.getMessage(), "", JOptionPane.ERROR_MESSAGE);
		}

		try {
			this.guimain.getPanelMain().getTxtTargetDir().setText(preset.getFileTargetDir().getAbsolutePath());
		} catch (TargetDirectoryNotFoundException e) {
			this.guimain.getPanelMain().getTxtTargetDir().setText(null);
			this.guimain.showMessage(e.getMessage(), "", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void startPreRefactor() {
		try {
			boolean locValid = this.guimmain.startPreRefactor();
		} catch (FileProcessException | SourceDirectoryNotFoundException | TargetDirectoryNotFoundException e) {
			this.guimain.showMessage(e.getMessage(), "", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void setPresetManager(File preset) {

		try {
			if (preset != null) {
				this.guimmain.setPresetManager(preset.getAbsolutePath());
			} else {
				this.guimmain.setPresetManagerDefault();
			}

			this.addPresetsToComboBox();
		} catch (PresetDirNotFoundException e) {
			this.guimain.showMessage(e.getMessage(), "", JOptionPane.ERROR_MESSAGE);
		}

		this.setPresetDirToTxf();
	}

	private void setPresetDirToTxf() {
		this.guimain.getPanelMain().getTxfPresetDir().setText(this.guimmain.getPresetDir());
	}

	private void addPresetsToComboBox() {
		List<Preset> locPresetList = this.guimmain.getPresetList();

		this.guimain.getPanelMain().getCbPreset().removeAllItems();
		
		for (Preset preset : locPresetList) {
			this.guimain.getPanelMain().getCbPreset().addItem(preset);
		}
	}
}
