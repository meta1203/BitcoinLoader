package com.meta1203;

import java.io.File;
import java.io.IOException;

import com.google.bitcoin.core.BlockChain;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.PeerGroup;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.discovery.DnsDiscovery;
import com.google.bitcoin.store.BlockStore;
import com.google.bitcoin.store.BlockStoreException;
import com.google.bitcoin.store.BoundedOverheadBlockStore;

public class SatoshisPreloader {
	public static void main(String[] args) throws IOException {
		Wallet wallet = null;
		try {
		    wallet = Wallet.loadFromFile(new File("wallet.wallet"));
		} catch (IOException e) {
            wallet = new Wallet(NetworkParameters.prodNet());
		    addAddressesToWallet(5, wallet);
		    try {
                wallet.saveToFile(new File("wallet.wallet"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		BlockStore block = null;
		BlockChain chain = null;
		try {
			block = new BoundedOverheadBlockStore(NetworkParameters.prodNet(), new File("store.blockchain"));
			chain = new BlockChain(NetworkParameters.prodNet(), wallet, block);
		} catch (BlockStoreException e) {
			e.printStackTrace();
		}
		PeerGroup peerGroup = new PeerGroup(NetworkParameters.prodNet(), chain);
		peerGroup.setUserAgent("SatoshisBukkit", "0.1");
		peerGroup.addWallet(wallet);
		peerGroup.addPeerDiscovery(new DnsDiscovery(NetworkParameters.prodNet()));
		peerGroup.start();
		
		peerGroup.downloadBlockChain();
		wallet.saveToFile(new File("wallet.wallet"));
		try {
			block.close();
		} catch (BlockStoreException e) {
			e.printStackTrace();
		}
	}
	
	private static void addAddressesToWallet(int num, Wallet wallet) {
		for (int x = 0; x < num; x++) {
			wallet.addKey(new ECKey());
		}
	}
}
