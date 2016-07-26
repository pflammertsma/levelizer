I'm Paul Lammertsma, the developer of an open source app called Levelizer. In short, Levelizer gives you haptic feedback when you're attempting to take an unlevel photo.

It works in such a way that you can use your favorite camera. Since the app runs as an accessibility service in the background, it simply monitors known (or manually whitelisted) camera apps as they are opened and closed. Your phone then gives you gentle taps until you're holding it more or less level.

There are some configuration options for the strength of the haptic feedback and the off-level tolerance, but that's literally it. Simple app for a simple problem!

I'm currently working on a screen overlay to indicate the correct orientation, and an overlaid button to easily toggle the feedback.

Together with fellow GDG lead Martin Liersch and GDG member Frank Bouwens, the three of us threw this during Droidcon Greece earlier this month.

The app can be downloaded here:
https://play.google.com/store/apps/details?id=org.dutchaug.levelizer

The source is available here:
https://github.com/pflammertsma/levelizer
