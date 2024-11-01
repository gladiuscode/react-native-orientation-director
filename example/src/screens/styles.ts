import { StyleSheet } from 'react-native';

export const homepageStyle = StyleSheet.create({
  container: {
    flexGrow: 1,
    padding: 20,
  },
  marginBottom: {
    marginBottom: 12,
  },
  buttonsContainer: {
    flex: 1,
    justifyContent: 'center',
  },
  text: {
    color: 'black',
  },
});

export const exploreStyle = StyleSheet.create({
  container: homepageStyle.container,
  marginBottom: homepageStyle.marginBottom,
  text: homepageStyle.text,
  body: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
});

export const innerExploreStyle = StyleSheet.create({
  ...exploreStyle,
});
