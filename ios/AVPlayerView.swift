//
//  AVPlayerView.swift
//  MyTransparentVideoExample
//
//  Created by Quentin on 27/10/2017.
//  Copyright © 2017 Quentin Fasquel. All rights reserved.
//

import AVFoundation
import UIKit

public class AVPlayerView: UIView {

    public var loopFrom: Int?

    deinit {
        playerItem = nil
    }
    
    public override class var layerClass: AnyClass {
        return AVPlayerLayer.self
    }
    
    public var playerLayer: AVPlayerLayer {
        return layer as! AVPlayerLayer
    }

    public private(set) var player: AVPlayer? {
        didSet {
            playerLayer.player = player
        }
    }

    private var playerItemStatusObserver: NSKeyValueObservation?

    private(set) var playerItem: AVPlayerItem? {
        didSet {
            setupLooping()
        }
    }
    
    public func loadPlayerItem(_ playerItem: AVPlayerItem, onReady: ((Result<AVPlayer, Error>) -> Void)? = nil) {
        let player = AVPlayer(playerItem: playerItem)

        self.player = player
        self.playerItem = playerItem

        guard let completion = onReady else {
            playerItemStatusObserver = nil
            return
        }

        playerItemStatusObserver = playerItem.observe(\.status) { [weak self] item, _ in
            switch item.status {
            case .failed:
                completion(.failure(item.error!))
            case .readyToPlay:
                completion(.success(player))
                // Stop observing
                self?.playerItemStatusObserver = nil
            case .unknown:
                break
            @unknown default:
                fatalError()
            }
        }
    }
        

    // MARK: - Looping Handler   
    /// When set to `true`, the player view automatically adds an observer on its AVPlayer,
    /// and it will play again from start every time playback ends.
    /// * Warning: This will not result in a smooth video loop.
    public var isLoopingEnabled: Bool = false {
        didSet { setupLooping() }
    }
    
    private var didPlayToEndTimeObsever: NSObjectProtocol? = nil {
        willSet(newObserver) {
            // When updating didPlayToEndTimeObserver,
            // automatically remove its previous value from the Notification Center
            if let observer = didPlayToEndTimeObsever, didPlayToEndTimeObsever !== newObserver {
                NotificationCenter.default.removeObserver(observer)
            }
        }
    }
    
    private func setupLooping() {
        guard let playerItem = self.playerItem, let player = self.player else {
            return
        }
        
        guard isLoopingEnabled else {
            didPlayToEndTimeObsever = nil
            return
        }

        let safeLoopFrom = loopFrom ?? 0  // Default value if loopFrom is nil
        let loopFromCMTime = CMTimeMake(value: Int64(safeLoopFrom), timescale: 1000)

        // let loopFromCMTime = CMTimeMake(value: Int64(loopFrom), timescale: 1000)
        didPlayToEndTimeObsever = NotificationCenter.default.addObserver(
            forName: .AVPlayerItemDidPlayToEndTime, object: playerItem, queue: nil, using: { _ in
                player.seek(to: loopFromCMTime) { _ in
                    player.play()
                }
        })
    }
    
}
