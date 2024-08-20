import React from 'react';

import ParallaxVideo, { ParallaxVideoConfig } from './ParallaxVideo';

interface ParallaxProps {
  layers: string[];
  config: ParallaxVideoConfig;
}

const Parallax = ({ layers, config }: ParallaxProps) => {
  return (
    <>
      {layers.map((layer: string, index: number) => (
        <ParallaxVideo
          key={'layer' + index}
          source={layer}
          zIndex={++index}
          config={config}
        />
      ))}
    </>
  );
};

export default Parallax;
