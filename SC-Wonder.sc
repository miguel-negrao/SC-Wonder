WonderBus {
	
	var <index;

	*new{ |plane =false, doppler = false|
		var i = WonderCtrl.allocator.alloc;
		if(i.notNil){
			WonderCtrl.addr.sendMsg('/WONDER/source/type',i,plane.binaryValue);
			WonderCtrl.addr.sendMsg('/WONDER/source/dopplerEffect',i,doppler.binaryValue);
			WonderCtrl.addr.sendMsg('/WONDER/source/activate',i);
			("creating Wonder WFS bus with index "++i).postln;
			^super.newCopyArgs(i)
		}{
			Error("ran out of WFS channels").throw;
		}
	}
	
	setPos_{ |x,y|
		("WonderBus "++index++": setting position to "++x++", "++y).postln;
		WonderCtrl.addr.sendMsg('/WONDER/source/position',x,y)
	}
	
	free{
		if(index.isNil)
			{ ("WonderBus has already been freed").warn; }
			{
				("WonderBus: freeing index "++index).postln;
				WonderCtrl.addr.sendMsg('/WONDER/source/deactivate',index);
				WonderCtrl.allocator.free(index);
				index = nil;
			}
	}
}

WonderCtrl{

	classvar <allocator, <addr, <server;
	
	*startup{ |startIndex = 0,size = 42,scsynthIP = '127.0.0.1',scsynthPort = 57119,makeDefault = true|
	
		var cwonderIP = "192.168.3.254",cwonderPort = 12222, options;
		allocator = ContiguousBlockAllocator(size+startIndex,startIndex);
		addr = NetAddr(cwonderIP,cwonderPort);
		options = ServerOptions()
			.numWireBufs_(1024)
			.numOutputBusChannels_(42)
			.remoteControlVolume_(true);
			
		server = Server("wonderSCSynth",NetAddr(scsynthIP.asString,scsynthPort),options);
		if(scsynthIP == '127.0.0.1'){
			server.boot
		};
		server.makeWindow;
		if(makeDefault){
			Server.default = server;
		};
	}
		
	
}