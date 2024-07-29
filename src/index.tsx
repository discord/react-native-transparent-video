import React from 'react';
import { requireNativeComponent, StyleProp, ViewStyle } from 'react-native';
// @ts-ignore
import resolveAssetSource from 'react-native/Libraries/Image/resolveAssetSource';

type TransparentVideoProps = {
  style: StyleProp<ViewStyle>;
  source?: any;
  loop?: boolean;
  autoplay?: boolean;
};

const ComponentName = 'TransparentVideoView';

const TransparentVideoView = requireNativeComponent(ComponentName);

class TransparentVideo extends React.PureComponent<TransparentVideoProps> {
  render() {
    const source = resolveAssetSource(this.props.source) || {
      uri: this.props.source,
    };
    let uri = source.uri || '';
    if (uri && uri.match(/^\//)) {
      uri = `file://${uri}`;
    }

    const nativeProps = Object.assign({}, this.props);
    Object.assign(nativeProps, {
      style: nativeProps.style,
      src: {
        uri,
        type: source.type || '',
      },
      autoplay: nativeProps.autoplay ?? true,
      loop: nativeProps.loop ?? true,
    });

    return <TransparentVideoView {...nativeProps} />;
  }
}

export default TransparentVideo;
